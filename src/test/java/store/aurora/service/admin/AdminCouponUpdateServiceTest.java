package store.aurora.service.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import store.aurora.domain.CouponState;
import store.aurora.dto.UpdateUserCouponDto;
import store.aurora.repository.*;
import store.aurora.service.AdminCouponService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class AdminCouponUpdateServiceTest {

    @Mock private CouponPolicyRepository couponPolicyRepository;
    @Mock private UserCouponRepository userCouponRepository;
    @Mock private DiscountRuleRepository disCountRuleRepository;
    @Mock private CategoryPolicyRepository categoryPolicyRepository;
    @Mock private BookPolicyRepository bookPolicyRepository;

    private AdminCouponService adminCouponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminCouponService = new AdminCouponService(
                couponPolicyRepository,
                userCouponRepository,
                disCountRuleRepository,
                categoryPolicyRepository,
                bookPolicyRepository
        );
    }

    @Test
    void couponUpdate_ShouldUpdateCouponState_WhenCouponStateIsProvided() {
        // Arrange
        List<Long> userCouponIds = Arrays.asList(1L, 2L, 3L);
        UpdateUserCouponDto dto = new UpdateUserCouponDto();
        dto.setUpdateUserIds(userCouponIds);
        dto.setUpdateState(CouponState.USED);

        // Act
        adminCouponService.couponUpdate(dto);

        // Assert
        verify(userCouponRepository, times(1)).updateCouponStateByUserIds(CouponState.USED, userCouponIds);
        verify(userCouponRepository, never()).updateCouponPolicyByUserIds(any(), any());
        verify(userCouponRepository, never()).updateCouponEndDateByUserIds(any(), any());
    }

    @Test
    void couponUpdate_ShouldUpdateCouponPolicy_WhenPolicyIdIsProvided() {
        // Arrange
        List<Long> userCouponIds = Arrays.asList(1L, 2L, 3L);
        UpdateUserCouponDto dto = new UpdateUserCouponDto();
        dto.setUpdateUserIds(userCouponIds);
        dto.setUpdatePolicyId(123L);

        // Act
        adminCouponService.couponUpdate(dto);

        // Assert
        verify(userCouponRepository, times(1)).updateCouponPolicyByUserIds(123L, userCouponIds);
        verify(userCouponRepository, never()).updateCouponStateByUserIds(any(), any());
        verify(userCouponRepository, never()).updateCouponEndDateByUserIds(any(), any());
    }

    @Test
    void couponUpdate_ShouldUpdateCouponEndDate_WhenEndDateIsProvided() {
        // Arrange
        List<Long> userCouponIds = Arrays.asList(1L, 2L, 3L);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        UpdateUserCouponDto dto = new UpdateUserCouponDto();
        dto.setUpdateUserIds(userCouponIds);
        dto.setUpdateEndDate(endDate);

        // Act
        adminCouponService.couponUpdate(dto);

        // Assert
        verify(userCouponRepository, times(1)).updateCouponEndDateByUserIds(endDate, userCouponIds);
        verify(userCouponRepository, never()).updateCouponStateByUserIds(any(), any());
        verify(userCouponRepository, never()).updateCouponPolicyByUserIds(any(), any());
    }

    @Test
    void couponUpdate_ShouldUpdateMultipleFields_WhenAllFieldsAreProvided() {
        // Arrange
        List<Long> userCouponIds = Arrays.asList(1L, 2L, 3L);
        UpdateUserCouponDto dto = new UpdateUserCouponDto();
        dto.setUpdateUserIds(userCouponIds);
        dto.setUpdateState(CouponState.TIMEOUT);
        dto.setUpdatePolicyId(456L);
        dto.setUpdateEndDate(LocalDate.of(2025, 12, 31));

        // Act
        adminCouponService.couponUpdate(dto);

        // Assert
        verify(userCouponRepository, times(1)).updateCouponStateByUserIds(CouponState.TIMEOUT, userCouponIds);
        verify(userCouponRepository, times(1)).updateCouponPolicyByUserIds(456L, userCouponIds);
        verify(userCouponRepository, times(1)).updateCouponEndDateByUserIds(LocalDate.of(2025, 12, 31), userCouponIds);
    }
}
