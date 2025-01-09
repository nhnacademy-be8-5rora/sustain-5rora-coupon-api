package store.aurora.scheduler.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import store.aurora.domain.CouponPolicy;
import store.aurora.domain.CouponState;
import store.aurora.domain.UserCoupon;
import store.aurora.feignclient.UserClient;
import store.aurora.repository.CouponPolicyRepository;
import store.aurora.repository.UserCouponRepository;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BirthDayCouponService {

    private final UserCouponRepository userCouponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private static final Logger USER_LOG = LoggerFactory.getLogger("user-logger");
    private final UserClient userClient;

    @Transactional
    public void createBirthCoupon(LocalDate now) {

        LocalDate lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());

        //feign client로 유저Id 받아옴.
        List<String> userIds = userClient.getUserIdByMonth(now.getMonthValue());

        //사용자 Id 갯수 출력
        USER_LOG.info("{}", userIds.size());

        // CouponPolicy 조회
        CouponPolicy policy = couponPolicyRepository.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("Invalid CouponPolicy ID"));

        //받아온 유저Id로 생일 쿠폰 생성(생일 쿠폰 정책 2L)
        List<UserCoupon> userCoupons = userIds.stream()
                .map(userId -> {
                    UserCoupon coupon = new UserCoupon();
                    coupon.setUserId(userId);
                    coupon.setPolicy(policy);
                    coupon.setStartDate(now);
                    coupon.setEndDate(lastDayOfMonth);
                    coupon.setCouponState(CouponState.LIVE);
                    return coupon;
                })
                .toList();

        userCouponRepository.saveAll(userCoupons);
    }
}
