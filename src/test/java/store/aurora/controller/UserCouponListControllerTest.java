package store.aurora.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import store.aurora.dto.response.UsedCouponDTO;
import store.aurora.dto.UserCouponDTO;
import store.aurora.service.CouponListService;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserCouponListController.class)
@ExtendWith(SpringExtension.class)
class UserCouponListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponListService couponListService;

    @Test
    @DisplayName("쿠폰 리스트 조회 테스트")
    void testGetCouponList() throws Exception {
        // Given
        String userId = "123";
        List<UserCouponDTO> userCouponList = List.of(
                new UserCouponDTO("Spring Coupon", 100, 500, 10, 50,
                        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                        List.of(1L, 2L), List.of(10L, 20L))
        );

        Mockito.when(couponListService.getCouponList(userId)).thenReturn(userCouponList);

        // When & Then
        mockMvc.perform(get("/api/coupon/list")
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].couponName").value("Spring Coupon"))
                .andExpect(jsonPath("$[0].needCost").value(100))
                .andExpect(jsonPath("$[0].maxSale").value(500))
                .andExpect(jsonPath("$[0].salePercent").value(10))
                .andExpect(jsonPath("$[0].saleAmount").value(50))
                .andExpect(jsonPath("$[0].startDate").value("2025-01-01"))
                .andExpect(jsonPath("$[0].endDate").value("2025-12-31"))
                .andExpect(jsonPath("$[0].bookIdList[0]").value(1L))
                .andExpect(jsonPath("$[0].categoryIdList[0]").value(10L));
    }

    @Test
    @DisplayName("사용한 쿠폰 리스트 조회 테스트")
    void testGetUsedCouponList() throws Exception {
        // Given
        String userId = "123";
        List<UsedCouponDTO> usedCouponList = List.of(
                new UsedCouponDTO("Discount Coupon",
                        LocalDate.of(2025, 1, 15),  //usedDate
                        LocalDate.of(2025, 1, 1),   //startDate
                        LocalDate.of(2025, 12, 31)) //endDate
        );

        Mockito.when(couponListService.getUsedCouponList(userId)).thenReturn(usedCouponList);

        // When & Then
        mockMvc.perform(get("/api/coupon/used/list")
                        .header("X-USER-ID", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].couponName").value("Discount Coupon"))
                .andExpect(jsonPath("$[0].usedDate").value("2025-01-15"))
                .andExpect(jsonPath("$[0].startDate").value("2025-01-01"))
                .andExpect(jsonPath("$[0].endDate").value("2025-12-31"));
    }
}