package store.aurora.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.domain.CouponPolicy;
import store.aurora.dto.ProductInfoDTO;
import store.aurora.domain.UserCoupon;
import store.aurora.dto.UserCouponDTO;
import store.aurora.dto.PaymentCouponDTO;
import store.aurora.mapper.UserCouponMapper;
import store.aurora.repository.CouponPolicyRepository;
import store.aurora.repository.UserCouponRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<Long, List<PaymentCouponDTO>> getCouponListByCategory(List<ProductInfoDTO> productInfoDTO, String userId) {
        Map<Long, List<PaymentCouponDTO>> couponListMap = new HashMap<>();    //상품별 사용가능한 쿠폰 맵

        //상품에 대한 필수 값들만 추출하여 한번에 처리
        List<Long> bookIds = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        Map<Long, Integer> priceMap = new HashMap<>();

        //상품 목록에서 필요한 값만 모아서 저장
        for (ProductInfoDTO product : productInfoDTO) {
            bookIds.add(product.getBookId());
            categoryIds.addAll(product.getCategoryIds());
            priceMap.put(product.getProductId(), product.getPrice());
        }

        //한 번에 모든 쿠폰을 조회
        List<CouponPolicy> couponList = getAvailableCouponList(bookIds, categoryIds, priceMap, userId);

        //상품별로 필터링 후 쿠폰 리스트 생성
        for (ProductInfoDTO product : productInfoDTO) {
            Long productId = product.getProductId();
            Integer totalPrice = product.getPrice();

            //상품에 해당하는 쿠폰만 필터링
            List<PaymentCouponDTO> resDiscountDTO = new ArrayList<>();
            for (CouponPolicy couponPolicy : couponList) {
                if (couponPolicyAppliesToProduct(couponPolicy, totalPrice)) {
                    resDiscountDTO.add(new PaymentCouponDTO(
                            couponPolicy.getName(),
                            couponPolicy.getDiscountRule().getNeedCost(),
                            couponPolicy.getDiscountRule().getMaxSale(),
                            couponPolicy.getDiscountRule().getSalePercent(),
                            couponPolicy.getDiscountRule().getSaleAmount()
                    ));
                }
            }

            couponListMap.put(productId, resDiscountDTO);
        }

        return couponListMap;
    }

    //한 번에 모든 쿠폰을 가져오는 최적화된 메서드
    public List<CouponPolicy> getAvailableCouponList(List<Long> bookIds, List<Long> categoryIds,
                                                     Map<Long, Integer> priceMap, String userId) {
        return userCouponRepository.findAvailableCouponsForBatch(userId, bookIds, categoryIds, priceMap);
    }

    //쿠폰이 상품에 적용될 수 있는지 확인하는 로직 (필터링)
    private boolean couponPolicyAppliesToProduct(CouponPolicy couponPolicy, Integer totalPrice) {
        //상품에 대한 조건에 맞는지 확인 (예: bookId, categoryId, totalPrice 등)
        boolean appliesToBook = couponPolicy.getBookPolicies().stream()
                .anyMatch(bp -> bp.getBookId() == null);
        boolean appliesToCategory = couponPolicy.getCategoryPolicies().stream()
                .anyMatch(cpCat -> cpCat.getCategoryId() == null);
        boolean appliesToPrice = couponPolicy.getDiscountRule().getNeedCost() == null ||
                couponPolicy.getDiscountRule().getNeedCost() <= totalPrice;

        return appliesToBook && appliesToCategory && appliesToPrice;
    }

    //쿠폰 정책 리스트 출력
    @Transactional(readOnly = true)
    public List<CouponPolicy> couponPolicyList() {
        return couponPolicyRepository.findAll();
    }
}
