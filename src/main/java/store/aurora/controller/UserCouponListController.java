package store.aurora.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.aurora.dto.ProductInfoDTO;
import store.aurora.dto.UserCouponDTO;
import store.aurora.service.CouponListService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class UserCouponListController {

    private final CouponListService couponListService;

    @GetMapping(value = "/list")
    public ResponseEntity<List<UserCouponDTO>> couponList(@RequestHeader(value = "X-USER-ID") String userId) {
        List<UserCouponDTO> userCouponList = couponListService.getCouponList(userId);

        return ResponseEntity.ok(userCouponList);
    }

    //결제창에서 상품마다 사용가능 쿠폰 리스트 확인(매 상품마다 사용 가능한 쿠폰이 뜨게 해야 됨.
    @GetMapping(value = "/usable")
    public ResponseEntity<Map<Long, List<String>>>proCouponList(@RequestHeader(value = "X-USER-ID") String userId,
                                                          @RequestBody @Validated List<ProductInfoDTO> productInfoDTO) {   //결제 API에서 필요한 값을 받아야함

        //orderId에 있는 카테고리, 북 ID을 불러와서 해당 사용자 쿠폰의 쿠폰정책과 비교해서 쓸 있는지 없는지 확인
        Map<Long, List<String>> userCoupons = couponListService.getCouponListByCategory(productInfoDTO, userId);

        return ResponseEntity.ok(userCoupons);
    }
}
