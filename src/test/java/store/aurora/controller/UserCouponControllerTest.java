package store.aurora.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.aurora.dto.PaymentCouponDTO;
import store.aurora.service.CouponListService;
import store.aurora.service.CouponService;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserCouponControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CouponService couponService;

    @Mock
    private CouponListService couponListService;

    @InjectMocks
    private UserCouponController userCouponController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userCouponController).build();
    }

    @Test
    void testGetCouponListByCategory() throws Exception {
        // Sample expected result
        PaymentCouponDTO coupon = new PaymentCouponDTO(1L, "Discount Coupon", 100, 50, 20, 10);
        List<PaymentCouponDTO> couponList = List.of(coupon);

        // Mock the couponListService response
        when(couponListService.getCouponListByCategory(anyString(), any(List.class)))
                .thenReturn(Map.of(1L, couponList));

        // Perform the test
        mockMvc.perform(post("/api/coupon/shop/usable")
                        .param("userId", "user123")  // Simulating the userId as a query parameter
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("["
                                + "{\"bookId\":1,\"categoryIds\":[101, 102],\"price\":500},"
                                + "{\"bookId\":2,\"categoryIds\":[103, 104],\"price\":300}"
                                + "]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['1'][0].id").value(1))
                .andExpect(jsonPath("$.['1'][0].couponName").value("Discount Coupon"))
                .andExpect(jsonPath("$.['1'][0].needCost").value(100))
                .andExpect(jsonPath("$.['1'][0].salePercent").value(10));
    }
}