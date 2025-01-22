package store.aurora.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.aurora.dto.*;

import store.aurora.domain.*;
import store.aurora.repository.*;

import java.util.List;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class AdminCouponService {

    private static final Logger USER_LOG = LoggerFactory.getLogger("user-logger");

    private final CouponPolicyRepository couponPolicyRepository;
    private final UserCouponRepository userCouponRepository;
    private final DiscountRuleRepository disCountRuleRepository;
    private  final CategoryPolicyRepository categoryPolicyRepository;
    private  final BookPolicyRepository bookPolicyRepository;

    //사용자 쿠폰 수정(요청한 쿠폰 유저 ID 리스트를 통해 해당 쿠폰들을 수정)
    @Transactional
    public void couponUpdate(UpdateUserCouponDto updateUserCouponDto) {
        CouponState couponState = updateUserCouponDto.getUpdateState();
        Long policyId = updateUserCouponDto.getUpdatePolicyId();
        LocalDate endDate = updateUserCouponDto.getUpdateEndDate();

        List<Long> userCouponIds = updateUserCouponDto.getUpdateUserIds();

        if (couponState != null) {
            userCouponRepository.updateCouponStateByUserIds(couponState, userCouponIds);
        }

        if (policyId != null) {
            userCouponRepository.updateCouponPolicyByUserIds(policyId, userCouponIds);
        }

        if (endDate != null) {
            userCouponRepository.updateCouponEndDateByUserIds(endDate, userCouponIds);
        }

    }

    //사용자 쿠폰 생성
    @Transactional
    public boolean userCouponCreate(RequestUserCouponDTO requestUserCouponDTO) {
        try {
            List<String> userIds = requestUserCouponDTO.getUserIds(); // 유저 ID 리스트
            Long policyId = requestUserCouponDTO.getCouponPolicyId(); // 적용할 정책
            CouponState state = requestUserCouponDTO.getState();   // 쿠폰 초기 상태
            LocalDate startDate = requestUserCouponDTO.getStartDate(); // 시작일
            LocalDate endDate = requestUserCouponDTO.getEndDate();     // 종료일


            CouponPolicy couponPolicy = couponPolicyRepository.findById(policyId).orElse(null);

            // 만약 정책이 없으면 false 반환
            if (couponPolicy == null) {
                return false;
            }

            List<UserCoupon> newCoupons = userIds.stream()
                    .map(userId -> {
                        UserCoupon userCoupon = new UserCoupon();
                        userCoupon.setUserId(userId); // 사용자 ID 설정
                        userCoupon.setPolicy(couponPolicy); // 정책 설정
                        userCoupon.setCouponState(state); // 초기 상태 설정
                        userCoupon.setStartDate(startDate); // 시작일 설정
                        userCoupon.setEndDate(endDate); // 종료일 설정
                        return userCoupon;
                    })
                    .toList();

            userCouponRepository.saveAll(newCoupons); // 한 번에 저장

            return true; // 성공적으로 저장되면 true 반환
        } catch (Exception e) {
            USER_LOG.info("사용자 쿠폰 생성 실패"); // 예외 발생 시 로그 출력
            return false; // 예외가 발생하면 false 반환
        }
    }

    //Welcome 쿠폰 확인
    @Transactional(readOnly = true)
    public boolean existWelcomeCoupon(String userId, Long policyId){
        return userCouponRepository.existsByUserIdAndPolicyId(userId, policyId);
    }

    //쿠폰 정책 생성(쿠폰계산 및 쿠폰 정책 개체 생성)
    @Transactional
    public void couponPolicyCreate(RequestCouponPolicyDTO requestCouponPolicyDTO
            , DiscountRuleDTO discountRuleDTO, AddPolicyDTO addPolicyDTO) {

        //계산 테이블 개체 생성
        DiscountRule discountRule = getDiscountRule(discountRuleDTO);
        disCountRuleRepository.save(discountRule);

        //쿠폰정책 기본 테이블 개체 생성
        CouponPolicy couponPolicy = new CouponPolicy();
        saveCouponPolicy(couponPolicy, requestCouponPolicyDTO, discountRule);

        //책, 카테고리 정책 테이블 생성
        saveCategoryPolicies(couponPolicy, addPolicyDTO);
        saveBookPolicies(couponPolicy, addPolicyDTO);
    }

    public void saveCouponPolicy(CouponPolicy couponPolicy, RequestCouponPolicyDTO requestCouponPolicyDTO, DiscountRule discountRule) {
        couponPolicy.setName(requestCouponPolicyDTO.getPolicyName());
        couponPolicy.setSaleType(requestCouponPolicyDTO.getSaleType());
        couponPolicy.setDiscountRule(discountRule);
        couponPolicyRepository.save(couponPolicy);
    }

    //계산룰 테이블 개체 생성
    private static DiscountRule getDiscountRule(DiscountRuleDTO discountRuleDTO) {
        DiscountRule discountRule = new DiscountRule();
        discountRule.setNeedCost(discountRuleDTO.getNeedCost());        //null 가능
        discountRule.setMaxSale(discountRuleDTO.getMaxSale());          //null 가능
        discountRule.setSalePercent(discountRuleDTO.getSalePercent());  //null 가능
        discountRule.setSaleAmount(discountRuleDTO.getSaleAmount());    //null 가능

        return discountRule;
    }

    //카테고리 정책 테이블 개체 생성(addPolicy -> categoryId, bookId list null 구분으로 테이블 생성)
    public void saveCategoryPolicies(CouponPolicy couponPolicy, AddPolicyDTO addPolicyDto) {
        if (addPolicyDto.getCategoryId() != null) {
            List<CategoryPolicy> categoryPolicies = addPolicyDto.getCategoryId().stream()
                    .map(categoryId -> {
                        CategoryPolicy categoryPolicy = new CategoryPolicy();
                        categoryPolicy.setPolicy(couponPolicy); // CouponPolicy 참조
                        categoryPolicy.setCategoryId(categoryId);
                        return categoryPolicy;
                    })
                    .toList();
            categoryPolicyRepository.saveAll(categoryPolicies);
        }
    }

    //북 정책 테이블 생성
    public void saveBookPolicies(CouponPolicy couponPolicy, AddPolicyDTO addPolicyDto) {
        if (addPolicyDto.getBookId() != null) {
            List<BookPolicy> bookPolicies = addPolicyDto.getBookId().stream()
                    .map(bookId -> {
                        BookPolicy bookPolicy = new BookPolicy();
                        bookPolicy.setPolicy(couponPolicy); // CouponPolicy 참조
                        bookPolicy.setBookId(bookId);
                        return bookPolicy;
                    })
                    .toList();
            bookPolicyRepository.saveAll(bookPolicies);
        }
    }
}