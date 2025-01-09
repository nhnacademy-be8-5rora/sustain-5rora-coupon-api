package store.aurora.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import store.aurora.scheduler.service.ExpiredUserCouponService;


@Component
@RequiredArgsConstructor
@Slf4j
public class ExpiredUserCouponScheduler {

    private final ExpiredUserCouponService expiredUserCouponService;

            // 매일 정오에 실행되는 스케줄러
    @Scheduled(cron = "0 0 12 * * ?") // 매일 12:00 PM 실행
    public void deleteExpiredScheduling() {
        expiredUserCouponService.deleteExpiredCoupons();
    }
}