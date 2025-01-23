package store.aurora.service.calculation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import store.aurora.domain.CouponState;
import store.aurora.domain.UserCoupon;
import store.aurora.repository.UserCouponRepository;
import store.aurora.service.CouponService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CouponRefundTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private UserCouponRepository userCouponRepository;

    private UserCoupon userCoupon1;
    private UserCoupon userCoupon2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        userCoupon1 = new UserCoupon();
        userCoupon1.setCouponId(1L);
        userCoupon1.setCouponState(CouponState.USED);
        userCoupon1.setUsedDate(LocalDate.now());

        userCoupon2 = new UserCoupon();
        userCoupon2.setCouponId(2L);
        userCoupon2.setCouponState(CouponState.LIVE);
        userCoupon2.setUsedDate(null);
    }

    @Test
    void testRefundSuccess() {
        // Given
        List<Long> couponIds = Arrays.asList(1L, 2L);
        userCoupon2.setCouponState(CouponState.USED);
        when(userCouponRepository.findByCouponIdIn(couponIds)).thenReturn(Arrays.asList(userCoupon1, userCoupon2));

        // When
        couponService.refund(couponIds);

        // Then
        verify(userCouponRepository, times(1)).saveAll(anyList());
        assertEquals(CouponState.LIVE, userCoupon1.getCouponState());
        assertNull(userCoupon1.getUsedDate());
    }

    @Test
    void testRefundNoCouponsFound() {
        // Given
        List<Long> couponIds = Arrays.asList(1L, 2L);
        when(userCouponRepository.findByCouponIdIn(couponIds)).thenReturn(Collections.emptyList());

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> couponService.refund(couponIds));
        assertEquals("No coupons found for the provided IDs.", thrown.getMessage());
    }

    @Test
    void testRefundCouponNotUsed() {
        // Given
        List<Long> couponIds = List.of(1L);
        when(userCouponRepository.findByCouponIdIn(couponIds)).thenReturn(Collections.singletonList(userCoupon2));

        // When & Then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> couponService.refund(couponIds));
        assertEquals("Cannot refund not used coupon: ID = LIVE", thrown.getMessage());
    }
}
