package store.aurora.mapper;

import store.aurora.domain.BookPolicy;
import store.aurora.domain.CategoryPolicy;
import store.aurora.domain.UserCoupon;
import store.aurora.dto.UserCouponDTO;
import java.util.List;

public class UserCouponMapper {

    private UserCouponMapper() {

    }

    // UserCoupon -> UserCouponDTO 변환
    public static UserCouponDTO toDTO(UserCoupon userCoupon) {
        UserCouponDTO dto = new UserCouponDTO();

        dto.setCouponName(userCoupon.getPolicy().getName());
        dto.setStartDate(userCoupon.getStartDate());
        dto.setEndDate(userCoupon.getEndDate());

        // 추가적으로 가져와야 하는 필드가 있으면 해당 값을 세팅
        dto.setNeedCost(userCoupon.getPolicy().getDiscountRule().getNeedCost()); // 예시로 policy 객체에서 가져온 값
        dto.setMaxSale(userCoupon.getPolicy().getDiscountRule().getMaxSale()); // policy에서 가져온 값
        dto.setSalePercent(userCoupon.getPolicy().getDiscountRule().getSalePercent()); // policy에서 가져온 값
        dto.setSaleAmount(userCoupon.getPolicy().getDiscountRule().getSaleAmount()); // policy에서 가져온 값

        // 예시로 bookIdList와 categoryIdList를 생성하는 로직이 필요하다면,
        // 서비스에서 적절하게 가져와서 설정
        dto.setBookIdList(getBookIdsForCoupon(userCoupon)); // 예시로 추가한 메서드
        dto.setCategoryIdList(getCategoryIdsForCoupon(userCoupon)); // 예시로 추가한 메서드

        return dto;
    }

    // CouponPolicy에 맞는 BookId List 가져오기
    private static List<Long> getBookIdsForCoupon(UserCoupon userCoupon) {
        // 예시 로직 - 실제 구현에 맞게 변경 필요
        return userCoupon.getPolicy().getBookPolicies().stream()
                .map(BookPolicy::getBookId) // Book 객체의 id 필드를 가져오는 로직
                .toList();
    }

    // CouponPolicy에 맞는 CategoryId List 가져오기
    private static List<Long> getCategoryIdsForCoupon(UserCoupon userCoupon) {
        // 예시 로직 - 실제 구현에 맞게 변경 필요
        return userCoupon.getPolicy().getCategoryPolicies().stream()
                .map(CategoryPolicy::getCategoryId) // Category 객체의 id 필드를 가져오는 로직
                .toList();
    }
}