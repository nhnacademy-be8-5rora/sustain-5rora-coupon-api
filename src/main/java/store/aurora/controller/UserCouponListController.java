package store.aurora.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import store.aurora.dto.UsedCouponDTO;
import store.aurora.dto.UserCouponDTO;
import store.aurora.service.CouponListService;

import java.util.List;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class UserCouponListController {

    private final CouponListService couponListService;

    @GetMapping(value = "/list")
    public List<UserCouponDTO> couponList(@RequestHeader(value = "X-USER-ID") String userId) {

        return couponListService.getCouponList(userId);
    }

    @GetMapping(value = "/used/list")
    public List<UsedCouponDTO> usedCouponList(@RequestHeader(value = "X-USER-ID") String userId) {
        return couponListService.getUsedCouponList(userId);
    }
}
