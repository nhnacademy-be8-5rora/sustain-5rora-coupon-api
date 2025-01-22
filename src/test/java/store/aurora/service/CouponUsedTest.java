package store.aurora.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import store.aurora.domain.CouponState;
import store.aurora.repository.UserCouponRepository;

import java.time.LocalDate;
import static org.mockito.Mockito.verify;

class CouponUsedTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // 초기화
    }

    @Test
    void testUsed() {
        Long userCouponId = 1L;  // 테스트할 쿠폰 ID
        LocalDate now = LocalDate.now();

        // 실제 메소드 호출
        couponService.used(userCouponId);

        // userCouponRepository의 liveToUsedByUserCouponId 메소드가 정확한 인자와 함께 호출되었는지 검증
        verify(userCouponRepository).liveToUsedByUserCouponId(userCouponId, CouponState.USED, now);
    }
}