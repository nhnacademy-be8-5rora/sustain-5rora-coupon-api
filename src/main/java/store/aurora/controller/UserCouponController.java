package store.aurora.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import store.aurora.dto.PaymentCouponDTO;
import store.aurora.dto.ProductInfoDTO;
import store.aurora.service.CouponListService;
import store.aurora.service.CouponService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupon/shop")
@RequiredArgsConstructor
public class UserCouponController {

    private final CouponService couponService;
    private final CouponListService couponListService;

    //사용자 쿠폰 환불시 해당 사용자 쿠폰 상태 변경 및 데이터베이스 동기화
    @PostMapping(value = "/refund")
    public ResponseEntity<String> userCouponRefund(@RequestBody @Valid List<Long> userCouponId) {

        couponService.refund(userCouponId);

        return ResponseEntity.ok("User Coupon refunded successfully.");
    }

    //사용자 쿠폰 사용시 해당 사용자 쿠폰 상태 변경 및 데이터베이스 동기화
    @PostMapping(value = "/using")
    public ResponseEntity<String> userCouponUsing(@RequestBody @Valid Long userCouponId){

        couponService.used(userCouponId);

        return ResponseEntity.ok("User Coupon used successfully.");
    }

    //사용가능한 쿠폰 정보 전달
    @PostMapping("/usable")
    Map<Long, List<PaymentCouponDTO>> getCouponListByCategory(@RequestParam @Valid String id,
                                                                  @RequestBody @Validated List<ProductInfoDTO> productInfoDTO){
        //orderId에 있는 카테고리, 북 ID을 불러와서 해당 사용자 쿠폰의 쿠폰정책과 비교해서 쓸 있는지 없는지 확인후 출력.
        //각 상품별로 NameWithDiscountDTO List 출력.
        return couponListService.getCouponListByCategory(id, productInfoDTO);
    }
}