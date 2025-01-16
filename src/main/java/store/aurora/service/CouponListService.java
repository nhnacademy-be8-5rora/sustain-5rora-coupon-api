package store.aurora.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.domain.CouponPolicy;
import store.aurora.domain.CouponState;
import store.aurora.dto.ProductInfoDTO;
import store.aurora.domain.UserCoupon;
import store.aurora.dto.UsedCouponDTO;
import store.aurora.dto.UserCouponDTO;
import store.aurora.dto.PaymentCouponDTO;
import store.aurora.mapper.UserCouponMapper;
import store.aurora.repository.CouponPolicyRepository;
import store.aurora.repository.UserCouponRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CouponListService {

    private final UserCouponRepository userCouponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    //사용자ID로 해당 사용자가 가진 사용자 쿠폰 목록 검색
    @Transactional(readOnly = true)
    public List<UserCouponDTO> getCouponList(String userId) {
        List<UserCoupon> userCoupons =userCouponRepository.findByUserId(userId);

        // List<UserCoupon>을 List<UserCouponDTO>로 변환
        return userCoupons.stream()
                .map(UserCouponMapper::toDTO) // toDTO 메서드를 사용하여 변환
                .toList();
    }

    //결제창에서 각 상품별 사용 가능 쿠폰 목록
    @Transactional(readOnly = true)
    public Map<Long, List<PaymentCouponDTO>> getCouponListByCategory(String id,
                                                          List<ProductInfoDTO> productInfoDTO) {
        Map<Long, List<PaymentCouponDTO>> productMap = new HashMap<>();
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(id);

        //productInfoDTO에서 나오는 bookId에 사용가능한 쿠폰 목록이 나오도록

        // 각 상품에 대해 사용 가능한 쿠폰 목록을 필터링하여 추가
        for (ProductInfoDTO productInfo : productInfoDTO) {
            List<PaymentCouponDTO> paymentCouponDTOS = new ArrayList<>();
            Long bookId = productInfo.getBookId();

            // 상품에 대한 사용 가능한 쿠폰을 필터링
            for (UserCoupon userCoupon : userCoupons) {
                // UserCoupon에서 정책을 가져옵니다.
                CouponPolicy couponPolicy = userCoupon.getPolicy();

                // 상품의 bookId와 쿠폰 정책을 비교하여 유효한 쿠폰을 찾습니다.
                if (isCouponAvailableForProduct(couponPolicy, productInfo)) {
                    // 쿠폰이 유효하다면, PaymentCouponDTO에 추가
                    paymentCouponDTOS.add(new PaymentCouponDTO(
                            userCoupon.getCouponId(),
                            couponPolicy.getName(),
                            couponPolicy.getDiscountRule().getNeedCost(),
                            couponPolicy.getDiscountRule().getMaxSale(),
                            couponPolicy.getDiscountRule().getSaleAmount(),
                            couponPolicy.getDiscountRule().getSaleAmount()
                    ));
                }
            }

            productMap.put(bookId, paymentCouponDTOS);
        }

        return productMap;
    }

    private boolean isCouponAvailableForProduct(CouponPolicy couponPolicy, ProductInfoDTO productInfo) {
        // 쿠폰 정책에서 bookId가 null이 아니면 bookId가 일치하는지 확인
        if (couponPolicy.getBookPolicies() != null && !couponPolicy.getBookPolicies().isEmpty()) {
            boolean matches = couponPolicy.getBookPolicies().stream()
                    .anyMatch(bookPolicy -> bookPolicy.getBookId().equals(productInfo.getBookId()));
            if (!matches) {
                return false; // bookId가 일치하지 않으면 사용 불가
            }
        }

        // 쿠폰 정책에서 categoryId가 null이 아니면 categoryId가 일치하는지 확인
        if (couponPolicy.getCategoryPolicies() != null && !couponPolicy.getCategoryPolicies().isEmpty()) {
            return !Collections.disjoint(couponPolicy.getCategoryPolicies(), productInfo.getCategoryIds());  // 교집합이 없으면 사용 불가
        }

        // 추가적인 쿠폰 유효성 검사 조건을 여기에 추가할 수 있습니다.

        return true;  // 모든 조건을 통과하면 쿠폰 사용 가능
    }

    //쿠폰 정책 리스트 출력
    @Transactional(readOnly = true)
    public List<CouponPolicy> couponPolicyList() {
        return couponPolicyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UsedCouponDTO> getUsedCouponList(String userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndState(userId, CouponState.USED);

        return userCoupons.stream()
                .map(userCoupon -> new UsedCouponDTO(
                        userCoupon.getPolicy().getName(),
                        userCoupon.getUsedDate(),
                        userCoupon.getEndDate(),
                        userCoupon.getStartDate()))
                .toList();
    }
}
