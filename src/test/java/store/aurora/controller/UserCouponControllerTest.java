package store.aurora.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.dto.PaymentCouponDTO;
import store.aurora.dto.ProductInfoDTO;
import store.aurora.service.CouponListService;
import store.aurora.service.CouponService;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserCouponController.class)
class UserCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;  // Mock 서비스

    @MockBean
    private CouponListService couponListService;  // Mock 서비스

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserCouponController(couponService, couponListService)).build();
    }

    @Test
    void testUserCouponRefund() throws Exception {
        // Given
        List<Long> userCouponIds = List.of(1L, 2L, 3L);

        // When
        doNothing().when(couponService).refund(userCouponIds);

        // Then
        mockMvc.perform(post("/api/coupon/shop/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2, 3]"))  // 요청 본문
                .andExpect(status().isOk())
                .andExpect(content().string("User Coupon refunded successfully."));

        // couponService.refund 메소드가 호출되는지 확인
        verify(couponService, times(1)).refund(userCouponIds);
    }

    @Test
    void testUserCouponUsing() throws Exception {
        // Given
        Long couponId = 1L;

        // When
        doNothing().when(couponService).used(couponId);

        // Then
        mockMvc.perform(post("/api/coupon/shop/using")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("1"))  // 요청 본문
                .andExpect(status().isOk())
                .andExpect(content().string("User Coupon used successfully."));

        // couponService.used 메소드가 호출되는지 확인
        verify(couponService, times(1)).used(couponId);
    }

    @Test
    void testGetCouponListByCategory() throws Exception {
        // Given
        String userId = "user1";
        ProductInfoDTO productInfoDTO = new ProductInfoDTO(1L, List.of(1L, 2L), 10000);
        List<ProductInfoDTO> productInfoDTOList = List.of(productInfoDTO);

        PaymentCouponDTO paymentCouponDTO = new PaymentCouponDTO(1L, "Coupon 1", null, null, null, 10);
        Map<Long, List<PaymentCouponDTO>> responseMap = Map.of(
                1L, List.of(paymentCouponDTO)
        );

        // When
        when(couponListService.getCouponListByCategory(userId, productInfoDTOList)).thenReturn(responseMap);

        // Then
        mockMvc.perform(post("/api/coupon/shop/usable")
                        .param("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"bookId\":1, \"categoryIds\":[1, 2], \"price\":10000}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['1'][0].id").value(1))
                .andExpect(jsonPath("$.['1'][0].couponName").value("Coupon 1"))
                .andExpect(jsonPath("$.['1'][0].salePercent").value(10));

        // couponListService.getCouponListByCategory 메소드가 호출되는지 확인
        verify(couponListService, times(1)).getCouponListByCategory(userId, productInfoDTOList);
    }
}