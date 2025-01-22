package store.aurora.scheduler;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.domain.CouponPolicy;
import store.aurora.domain.CouponState;
import store.aurora.domain.UserCoupon;
import store.aurora.feignclient.UserClient;
import store.aurora.repository.CouponPolicyRepository;
import store.aurora.repository.UserCouponRepository;
import store.aurora.scheduler.service.BirthDayCouponService;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BirthDayCouponServiceTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private BirthDayCouponService birthDayCouponService;

    private LocalDate now;

    @BeforeEach
    void setUp() {
        now = LocalDate.of(2025, Month.JANUARY, 21);
    }

    @Test
    @Transactional
    void createBirthCoupon_shouldCreateCouponsForUsers() {

        // Arrange
        List<String> userIds = Arrays.asList("user1", "user2", "user3");

        when(userClient.getUserIdByMonth(now.getMonthValue())).thenReturn(userIds);

        CouponPolicy policy = new CouponPolicy();
        policy.setId(2L);
        when(couponPolicyRepository.findById(2L)).thenReturn(java.util.Optional.of(policy));

        // Act
        birthDayCouponService.createBirthCoupon(now);

        // Capture the arguments passed to the saveAll method
        ArgumentCaptor<List<UserCoupon>> captor = ArgumentCaptor.forClass(List.class);
        verify(userCouponRepository).saveAll(captor.capture());

        // Assert
        List<UserCoupon> savedCoupons = captor.getValue();

        assertNotNull(savedCoupons);
        assertEquals(3, savedCoupons.size());  // 3개의 유저 쿠폰이 저장되었는지 확인
        assertEquals(userIds.size(), savedCoupons.size());
        for (UserCoupon coupon : savedCoupons) {
            assertEquals(policy, coupon.getPolicy());
            assertEquals(CouponState.LIVE, coupon.getCouponState());
            assertEquals(now, coupon.getStartDate());
            assertEquals(now.withMonth(now.getMonthValue()).with(TemporalAdjusters.lastDayOfMonth()), coupon.getEndDate());
        }
    }
}