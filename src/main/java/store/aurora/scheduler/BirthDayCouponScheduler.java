package store.aurora.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import store.aurora.scheduler.service.BirthDayCouponService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class BirthDayCouponScheduler {

    private final BirthDayCouponService birthDayCouponService;


    // 매달 1일 자정에 실행
    @Scheduled(cron = "0 0 0 1 * *") // cron 표현식: 매달 1일 00:00
    public void executeMonthlyBirthdayCouponCheck() {
        LocalDate now = LocalDate.now();

        birthDayCouponService.createBirthCoupon(now);
    }
}