package store.aurora;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import store.aurora.controller.WelcomeCouponController;
import store.aurora.domain.CouponState;
import store.aurora.dto.RequestUserCouponDTO;
import store.aurora.service.AdminCouponService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(WelcomeCouponController.class)
class WelcomeCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminCouponService adminCouponService;

    private String userId;

    @BeforeEach
    public void setUp() {
        userId = "user123";  // 예시 사용자 ID
        RequestUserCouponDTO requestUserCouponDTO = new RequestUserCouponDTO();
        requestUserCouponDTO.setUserIds(List.of(userId));
        requestUserCouponDTO.setCouponPolicyId(1L);  // 환영 쿠폰 정책 ID
        requestUserCouponDTO.setStartDate(LocalDate.now());
        requestUserCouponDTO.setEndDate(LocalDate.now().plusDays(30));
        requestUserCouponDTO.setState(CouponState.LIVE);
    }

    @Test
    void testRegisterUserWithExistingCoupon() throws Exception {
        // 이미 쿠폰이 발급된 경우
        when(adminCouponService.existWelcomeCoupon(userId, 1L)).thenReturn(true);

        mockMvc.perform(post("/api/coupon/welcome")
                        .header("X-USER-ID", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("이미 Welcome 쿠폰이 발급되었습니다."));
    }

    @Test
    void testRegisterUserWithNewCoupon() throws Exception {
        // 쿠폰이 발급되지 않은 경우
        when(adminCouponService.existWelcomeCoupon(userId, 1L)).thenReturn(false);
        when(adminCouponService.userCouponCreate(Mockito.any(RequestUserCouponDTO.class))).thenReturn(true);

        mockMvc.perform(post("/api/coupon/welcome")
                        .header("X-USER-ID", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome 쿠폰 발급 요청이 처리되었습니다."));
    }

    @Test
    void testSignUpWelcomeCoupon() throws Exception {
        // 쿠폰 발급 실패 시
        when(adminCouponService.existWelcomeCoupon(userId, 1L)).thenReturn(false);
        when(adminCouponService.userCouponCreate(Mockito.any(RequestUserCouponDTO.class))).thenReturn(false);

        mockMvc.perform(post("/api/coupon/signup/welcome")
                        .param("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome 쿠폰 발급 요청이 실패되었습니다. 재발급 버튼을 눌러주세요."));
    }
}
