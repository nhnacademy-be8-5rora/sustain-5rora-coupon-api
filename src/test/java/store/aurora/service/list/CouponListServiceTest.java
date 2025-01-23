package store.aurora.service.list;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.domain.CouponPolicy;
import store.aurora.domain.CouponState;
import store.aurora.domain.UserCoupon;
import store.aurora.dto.response.UsedCouponDTO;
import store.aurora.repository.UserCouponRepository;
import store.aurora.service.CouponListService;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CouponListServiceTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponListService couponListService;

    @Test
    void testGetUsedCouponList() {
        // given
        String userId = "user123";
        List<UserCoupon> mockCoupons = List.of(
                createUserCoupon("Coupon A", LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 10)),
                createUserCoupon("Coupon B", LocalDate.of(2025, 1, 5),
                        LocalDate.of(2025, 1, 20), LocalDate.of(2025, 1, 15))
        );

        Mockito.when(userCouponRepository.findByUserIdAndState(userId, CouponState.USED))
                .thenReturn(mockCoupons);

        // when
        List<UsedCouponDTO> usedCoupons = couponListService.getUsedCouponList(userId);

        // then
        Assertions.assertNotNull(usedCoupons);
        Assertions.assertEquals(2, usedCoupons.size());

        UsedCouponDTO firstCoupon = usedCoupons.getFirst();
        Assertions.assertEquals("Coupon A", firstCoupon.getCouponName());
        Assertions.assertEquals(LocalDate.of(2025, 1, 10), firstCoupon.getUsedDate());
        Assertions.assertEquals(LocalDate.of(2025, 1, 1), firstCoupon.getStartDate());
        Assertions.assertEquals(LocalDate.of(2025, 1, 15), firstCoupon.getEndDate());

        UsedCouponDTO secondCoupon = usedCoupons.get(1);
        Assertions.assertEquals("Coupon B", secondCoupon.getCouponName());
        Assertions.assertEquals(LocalDate.of(2025, 1, 15), secondCoupon.getUsedDate());
        Assertions.assertEquals(LocalDate.of(2025, 1, 5), secondCoupon.getStartDate());
        Assertions.assertEquals(LocalDate.of(2025, 1, 20), secondCoupon.getEndDate());

        Mockito.verify(userCouponRepository, Mockito.times(1)).findByUserIdAndState(userId, CouponState.USED);
    }

    private UserCoupon createUserCoupon(String couponName, LocalDate startDate, LocalDate endDate, LocalDate usedDate) {
        CouponPolicy couponPolicy = new CouponPolicy();
        couponPolicy.setName(couponName);

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setPolicy(couponPolicy);
        userCoupon.setCouponState(CouponState.USED);
        userCoupon.setStartDate(startDate);
        userCoupon.setEndDate(endDate);
        userCoupon.setUsedDate(usedDate);
        return userCoupon;
    }
}
