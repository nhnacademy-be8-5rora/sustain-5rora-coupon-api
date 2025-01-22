package store.aurora.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import store.aurora.repository.UserCouponRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class WelcomeCouponServiceTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private AdminCouponService adminCouponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExistWelcomeCoupon_whenCouponExists() {
        // Given
        String userId = "user123";
        Long policyId = 1L;
        when(userCouponRepository.existsByUserIdAndPolicyId(userId, policyId)).thenReturn(true);

        // When
        boolean result = adminCouponService.existWelcomeCoupon(userId, policyId);

        // Then
        assertTrue(result, "The coupon should exist for the given user and policy");
        verify(userCouponRepository, times(1)).existsByUserIdAndPolicyId(userId, policyId);
    }

    @Test
    void testExistWelcomeCoupon_whenCouponDoesNotExist() {
        // Given
        String userId = "user123";
        Long policyId = 2L;
        when(userCouponRepository.existsByUserIdAndPolicyId(userId, policyId)).thenReturn(false);

        // When
        boolean result = adminCouponService.existWelcomeCoupon(userId, policyId);

        // Then
        assertFalse(result, "The coupon should not exist for the given user and policy");
        verify(userCouponRepository, times(1)).existsByUserIdAndPolicyId(userId, policyId);
    }
}
