package store.aurora.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.aurora.domain.CouponState;
import store.aurora.domain.UserCoupon;
import store.aurora.repository.UserCouponRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final UserCouponRepository userCouponRepository;

    //사용자 쿠폰 환불
    @Transactional
    public void refund(String userCouponId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userCouponId);
        if (userCoupons.isEmpty()) {
            throw new IllegalArgumentException("No coupons found for the provided IDs.");
        }

        //지정된 사용자 쿠폰들 중에서 couponStat.USED가 아닌 것일 경우 오류처리
        for (UserCoupon userCoupon : userCoupons) {
            if (userCoupon.getCouponState() != CouponState.USED) {
                throw new IllegalStateException("Cannot refund not used coupon: ID = " + userCoupon.getCouponState());
            }

            userCoupon.setCouponState(CouponState.LIVE);    //사용자 쿠폰 상태 live 변경(used -> live)
            userCoupon.setUsedDate(null);    //사용기간 null로 변경
        }

        userCouponRepository.saveAll(userCoupons); // 상태 변경 후 저장

    }

    //사용자 쿠폰 사용
    @Transactional
    public void used(String userCouponId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userCouponId);
        if (userCoupons.isEmpty()) {
            throw new IllegalArgumentException("No coupons found for the provided IDs.");
        }

        for (UserCoupon userCoupon : userCoupons) {
            if (userCoupon.getCouponState() != CouponState.LIVE) {
                throw new IllegalStateException("Cannot use a dead coupon: ID = " + userCoupon.getCouponState());
            }

            userCoupon.setCouponState(CouponState.USED);    //Live -> used 변경
            userCoupon.setUsedDate(LocalDate.now());
        }

        userCouponRepository.saveAll(userCoupons); // 상태 변경 후 저장
    }
}
