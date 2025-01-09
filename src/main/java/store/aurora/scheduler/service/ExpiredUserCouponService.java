package store.aurora.scheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import store.aurora.repository.UserCouponRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpiredUserCouponService {
    private final UserCouponRepository userCouponRepository;
    private static final Logger USER_LOG = LoggerFactory.getLogger("user-logger");

    // 매일 정오에 실행되는 스케줄러
    public void deleteExpiredCoupons() {
        //sed나 timeout인 상태의 사용자 쿠폰의 데이터를 삭제
        // 현재시간에서 만료기간이 90일 이상인 사용자 쿠폰을 삭제.
        userCouponRepository.deleteExpiredCoupons(LocalDate.now().minusDays(90));
        USER_LOG.info("Expired coupons delete at: {}", java.time.LocalDateTime.now());

        //live 인 쿠폰 중에서 만료기간보다 현재기간이 큰 경우에만 couponState 을 timeout 으로 변경하는 쿼리
        userCouponRepository.updateExpiredCoupons();
        USER_LOG.info("Expired coupons updated at: {}", java.time.LocalDateTime.now());
    }
}
