package store.aurora.scheduler;

import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import store.aurora.scheduler.service.BirthDayCouponService;

@SpringBootTest
class BirthDayCouponSchedulerTest {

    @Autowired
    private BirthDayCouponScheduler birthDayCouponScheduler;

    @MockBean
    private BirthDayCouponService birthDayCouponService;

    @BeforeEach
    void setUp() {
        // 필요한 경우 추가적인 설정을 할 수 있습니다.
    }

    @Test
    void testExecuteMonthlyBirthdayCouponCheck() {
        // Given: 오늘 날짜
        LocalDate now = LocalDate.now();

        // When: executeMonthlyBirthdayCouponCheck 호출
        birthDayCouponScheduler.executeMonthlyBirthdayCouponCheck();

        // Then: birthDayCouponService.createBirthCoupon이 호출되었는지 확인
        verify(birthDayCouponService, times(1)).createBirthCoupon(now);
    }
}