package store.aurora.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.aurora.domain.*;
import store.aurora.dto.UserCouponDTO;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCouponMapperTest {

    private UserCouponMapper userCouponMapper;

    @BeforeEach
    void setUp() {
        userCouponMapper = new UserCouponMapper();
    }

    @Test
    void testToDTO() {
        // Arrange
        DiscountRule discountRule = mock(DiscountRule.class);
        when(discountRule.getNeedCost()).thenReturn(5000);
        when(discountRule.getMaxSale()).thenReturn(1000);
        when(discountRule.getSalePercent()).thenReturn(10);
        when(discountRule.getSaleAmount()).thenReturn(500);

        BookPolicy bookPolicy1 = mock(BookPolicy.class);
        when(bookPolicy1.getBookId()).thenReturn(1L);
        BookPolicy bookPolicy2 = mock(BookPolicy.class);
        when(bookPolicy2.getBookId()).thenReturn(2L);

        CategoryPolicy categoryPolicy1 = mock(CategoryPolicy.class);
        when(categoryPolicy1.getCategoryId()).thenReturn(100L);
        CategoryPolicy categoryPolicy2 = mock(CategoryPolicy.class);
        when(categoryPolicy2.getCategoryId()).thenReturn(200L);

        CouponPolicy policy = mock(CouponPolicy.class);
        when(policy.getName()).thenReturn("Sample Coupon");
        when(policy.getDiscountRule()).thenReturn(discountRule);
        when(policy.getBookPolicies()).thenReturn(List.of(bookPolicy1, bookPolicy2));
        when(policy.getCategoryPolicies()).thenReturn(List.of(categoryPolicy1, categoryPolicy2));

        UserCoupon userCoupon = mock(UserCoupon.class);
        when(userCoupon.getPolicy()).thenReturn(policy);
        when(userCoupon.getStartDate()).thenReturn(LocalDate.of(2025, 1, 1));
        when(userCoupon.getEndDate()).thenReturn(LocalDate.of(2025, 12, 31));

        // Act
        UserCouponDTO dto = userCouponMapper.toDTO(userCoupon);

        // Assert
        assertNotNull(dto);
        assertEquals("Sample Coupon", dto.getCouponName());
        assertEquals(LocalDate.of(2025, 1, 1), dto.getStartDate());
        assertEquals(LocalDate.of(2025, 12, 31), dto.getEndDate());
        assertEquals(5000, dto.getNeedCost());
        assertEquals(1000, dto.getMaxSale());
        assertEquals(10, dto.getSalePercent());
        assertEquals(500, dto.getSaleAmount());
        assertEquals(List.of(1L, 2L), dto.getBookIdList());
        assertEquals(List.of(100L, 200L), dto.getCategoryIdList());
    }
}
