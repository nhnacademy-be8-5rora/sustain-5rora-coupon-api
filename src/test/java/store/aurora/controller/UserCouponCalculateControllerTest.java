package store.aurora.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import store.aurora.service.CouponListService;
import store.aurora.service.CouponService;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(UserCouponController.class)
class UserCouponCalculateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @MockBean
    private CouponListService couponListService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUserCouponRefund() throws Exception {
        // Given
        List<Long> userCouponIds = List.of(1L, 2L, 3L);
        doNothing().when(couponService).refund(userCouponIds);

        // When & Then
        mockMvc.perform(post("/api/coupon/shop/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCouponIds)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Coupon refunded successfully."));
    }

    @Test
    void testUserCouponUsing() throws Exception {
        // Given
        Long couponId = 1L;
        doNothing().when(couponService).used(couponId);

        // When & Then
        mockMvc.perform(post("/api/coupon/shop/using")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(couponId)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Coupon used successfully."));
    }
}