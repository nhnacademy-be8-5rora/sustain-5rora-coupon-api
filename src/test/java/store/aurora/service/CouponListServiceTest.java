package store.aurora.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.domain.CouponPolicy;
import store.aurora.domain.DiscountRule;
import store.aurora.domain.UserCoupon;
import store.aurora.dto.UserCouponDTO;
import store.aurora.mapper.UserCouponMapper;
import store.aurora.repository.UserCouponRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponListServiceTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private UserCouponMapper userCouponMapper;

    @InjectMocks
    private CouponListService couponListService;

    @Test
    void getCouponList_ShouldReturnCouponDTOList_WhenCouponsExist() {
        // Arrange
        String userId = "testUser";

        UserCoupon mockCoupon = getUserCoupon(userId);

        UserCouponDTO mockCouponDTO = new UserCouponDTO();
        mockCouponDTO.setCouponName("Test Coupon");
        mockCouponDTO.setStartDate(LocalDate.of(2025, 1, 1));
        mockCouponDTO.setEndDate(LocalDate.of(2025, 12, 31));
        mockCouponDTO.setNeedCost(100);
        mockCouponDTO.setMaxSale(50);
        mockCouponDTO.setSalePercent(10);
        mockCouponDTO.setSaleAmount(20);

        List<UserCoupon> mockCoupons = List.of(mockCoupon);

        when(userCouponRepository.findByUserId(userId)).thenReturn(mockCoupons);
        when(userCouponMapper.toDTO(mockCoupon)).thenReturn(mockCouponDTO);

        // Act
        List<UserCouponDTO> result = couponListService.getCouponList(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Coupon", result.getFirst().getCouponName());
        assertEquals(100, result.getFirst().getNeedCost());
        assertEquals(50, result.getFirst().getMaxSale());
        assertEquals(10, result.getFirst().getSalePercent());
        assertEquals(20, result.getFirst().getSaleAmount());
        assertEquals(LocalDate.of(2025, 1, 1), result.getFirst().getStartDate());
        assertEquals(LocalDate.of(2025, 12, 31), result.getFirst().getEndDate());

        verify(userCouponRepository, times(1)).findByUserId(userId);
        verify(userCouponMapper, times(1)).toDTO(mockCoupon);
    }

    private static UserCoupon getUserCoupon(String userId) {
        UserCoupon mockCoupon = new UserCoupon();
        mockCoupon.setCouponId(1L);
        mockCoupon.setUserId(userId);

        CouponPolicy policy = new CouponPolicy();
        policy.setName("Test Coupon");

        DiscountRule discountRule = new DiscountRule();
        discountRule.setNeedCost(100);
        discountRule.setMaxSale(50);
        discountRule.setSalePercent(10);
        discountRule.setSaleAmount(20);
        policy.setDiscountRule(discountRule);

        mockCoupon.setPolicy(policy);
        mockCoupon.setStartDate(LocalDate.of(2025, 1, 1));
        mockCoupon.setEndDate(LocalDate.of(2025, 12, 31));
        return mockCoupon;
    }

    @Test
    void getCouponList_ShouldReturnEmptyList_WhenNoCouponsExist() {
        // Arrange
        String userId = "testUser";
        when(userCouponRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        List<UserCouponDTO> result = couponListService.getCouponList(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userCouponRepository, times(1)).findByUserId(userId);
        verifyNoInteractions(userCouponMapper);
    }
}