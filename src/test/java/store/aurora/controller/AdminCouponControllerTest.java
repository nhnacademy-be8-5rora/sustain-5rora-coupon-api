package store.aurora.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import store.aurora.service.AdminCouponService;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCouponController.class)
class AdminCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminCouponService adminCouponService;

    @Test
    void testCouponPolicyCreate_Success() throws Exception {
        // Given
        String requestJson = """
                {
                    "discountRuleDTO": {
                        "salePercent": 10
                    },
                    "addPolicyDTO": {
                        "policyName": "New Year Discount"
                    }
                }
                """;

        doNothing().when(adminCouponService).couponPolicyCreate(Mockito.any(), Mockito.any(), Mockito.any());

        // When & Then
        mockMvc.perform(post("/api/coupon/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("쿠폰정보가 생성되었습니다."));
    }

    @Test
    void testCouponPolicyCreate_Failure_InvalidDiscount() throws Exception {
        // Given
        String requestJson = """
                {
                    "discountRuleDTO": {
                        "salePercent": null,
                        "saleAmount": null
                    },
                    "addPolicyDTO": {
                        "policyName": "New Year Discount"
                    }
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/coupon/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    assertThat(result.getResolvedException())
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage("할인율과 할인량이 둘 다 없이 불가.");
                    assertThat(Objects.requireNonNull(result.getResolvedException()).getMessage())
                            .contains("할인율과 할인량이 둘 다 없이 불가.");
                });
    }

    @Test
    void testUserCouponCreate_Success() throws Exception {
        // Given
        String requestJson = """
                {
                    "discountRuleDTO": {
                        "salePercent": null,
                        "saleAmount": 10000
                    },
                    "addPolicyDTO": {
                        "categoryId": [1, 2],
                        "bookId": [3, 4]
                    }
                }
                """;

        doNothing().when(adminCouponService).couponPolicyCreate(Mockito.any(), Mockito.any(), Mockito.any());

        // When & Then
        mockMvc.perform(post("/api/coupon/admin/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("쿠폰정보가 생성되었습니다."));
    }

    @Test
    void testCouponUpdate_Success() throws Exception {
        // Given
        String requestJson = """
                {
                    "userCouponId": 202,
                    "status": "EXPIRED"
                }
                """;

        doNothing().when(adminCouponService).couponUpdate(Mockito.any());

        // When & Then
        mockMvc.perform(post("/api/coupon/admin/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("사용자쿠폰이 수정되었습니다."));
    }

    @Test
    void testCouponUpdate_Failure() throws Exception {
        // Given
        String requestJson = """
                {
                    "userCouponId": null,
                    "status": "EXPIRED"
                }
                """;

        doThrow(new IllegalArgumentException("사용자 쿠폰 ID는 필수입니다."))
                .when(adminCouponService).couponUpdate(Mockito.any());

        // When & Then
        mockMvc.perform(post("/api/coupon/admin/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("사용자 쿠폰 ID는 필수입니다."));
    }
}