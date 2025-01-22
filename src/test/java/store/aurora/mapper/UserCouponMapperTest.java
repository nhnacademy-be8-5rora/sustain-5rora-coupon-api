package store.aurora.mapper;

import org.junit.jupiter.api.Test;
import store.aurora.domain.*;
import store.aurora.dto.UserCouponDTO;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserCouponMapperTest {

    @Test
    void testToDTO() {
        // Given: 테스트에 필요한 객체 생성 및 설정
        DiscountRule discountRule = new DiscountRule();
        discountRule.setNeedCost(10000);
        discountRule.setMaxSale(5000);
        discountRule.setSalePercent(10);
        discountRule.setSaleAmount(1000);

        CouponPolicy policy = new CouponPolicy();
        policy.setName("Spring Sale");
        policy.setDiscountRule(discountRule);
        policy.setBookPolicies(List.of(
                createBookPolicy(policy, 101L),
                createBookPolicy(policy, 102L)
        ));
        policy.setCategoryPolicies(List.of(
                createCategoryPolicy(policy, 201L),
                createCategoryPolicy(policy, 202L)
        ));

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setPolicy(policy);
        userCoupon.setStartDate(LocalDate.of(2025, 1, 1));
        userCoupon.setEndDate(LocalDate.of(2025, 12, 31));
        userCoupon.setCouponId(1L);

        // When: Mapper를 호출하여 DTO 생성
        UserCouponDTO dto = UserCouponMapper.toDTO(userCoupon);

        // Then: DTO의 필드 값이 예상과 일치하는지 확인
        assertEquals("Spring Sale", dto.getCouponName());
        assertEquals(10000, dto.getNeedCost());
        assertEquals(5000, dto.getMaxSale());
        assertEquals(10, dto.getSalePercent());
        assertEquals(1000, dto.getSaleAmount());
        assertEquals(LocalDate.of(2025, 1, 1), dto.getStartDate());
        assertEquals(LocalDate.of(2025, 12, 31), dto.getEndDate());
        assertEquals(List.of(101L, 102L), dto.getBookIdList());
        assertEquals(List.of(201L, 202L), dto.getCategoryIdList());
    }

    // Helper 메서드: BookPolicy 생성
    private BookPolicy createBookPolicy(CouponPolicy policy, Long bookId) {
        BookPolicy bookPolicy = new BookPolicy();
        bookPolicy.setPolicy(policy);
        bookPolicy.setBookId(bookId);
        return bookPolicy;
    }

    // Helper 메서드: CategoryPolicy 생성
    private CategoryPolicy createCategoryPolicy(CouponPolicy policy, Long categoryId) {
        CategoryPolicy categoryPolicy = new CategoryPolicy();
        categoryPolicy.setPolicy(policy);
        categoryPolicy.setCategoryId(categoryId);
        return categoryPolicy;
    }
}
