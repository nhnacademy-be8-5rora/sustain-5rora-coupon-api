package store.aurora.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import store.aurora.dto.RequestUserCouponDTO;
import store.aurora.service.AdminCouponService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class WelcomeCouponController {

    private final AdminCouponService adminCouponService;

    // 회원가입 API
    @PostMapping("/welcome")
    public String registerUser(@RequestHeader(value = "X-USER-ID") String userId) {

        // 사용자가 이미 Welcome 쿠폰을 보유하고 있는지 확인
        boolean alreadyHasCoupon = adminCouponService.existWelcomeCoupon(userId, 1L);

        if (alreadyHasCoupon) {
            return "이미 Welcome 쿠폰이 발급되었습니다.";
        }

        LocalDate currentDate = LocalDate.now();
        //WelcomeCoupon 생성(5만원 이상 구매시 1만 할인 쿠폰, 기간 30일)
        RequestUserCouponDTO requestUserCouponDTO = new RequestUserCouponDTO();
        requestUserCouponDTO.setUserIds(List.of(userId));
        requestUserCouponDTO.setCouponPolicyId(1L); //사용자 환영 쿠폰 정책
        requestUserCouponDTO.setStartDate(currentDate);
        requestUserCouponDTO.setStartDate(currentDate.plusDays(30));

        boolean success = adminCouponService.userCouponCreate(requestUserCouponDTO);

        if (success) {
            return "회원가입 성공! Welcome 쿠폰 발급 요청이 처리되었습니다.";
        }

        return "Welcome 쿠폰 발급 요청이 실패되었습니다. 재발급 버튼을 눌러주세요.";
    }
}
