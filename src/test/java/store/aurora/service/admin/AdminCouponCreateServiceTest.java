package store.aurora.service.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import store.aurora.domain.CouponPolicy;
import store.aurora.domain.CouponState;
import store.aurora.domain.DiscountRule;
import store.aurora.domain.SaleType;
import store.aurora.dto.RequestUserCouponDTO;
import store.aurora.repository.CouponPolicyRepository;
import store.aurora.repository.UserCouponRepository;
import store.aurora.service.AdminCouponService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
class AdminCouponCreateServiceTest {

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private AdminCouponService adminCouponService;

    private CouponPolicy couponPolicy;
    private RequestUserCouponDTO requestUserCouponDTO;

    @BeforeEach
    void setUp() {
        couponPolicy = new CouponPolicy();
        couponPolicy.setId(1L); // 정책 ID 설정
        couponPolicy.setName("Test Policy");
        couponPolicy.setSaleType(SaleType.PERCENT);
        couponPolicy.setDiscountRule(new DiscountRule());  // 가짜 할인 규칙 설정

        requestUserCouponDTO = new RequestUserCouponDTO();
        requestUserCouponDTO.setUserIds(List.of("user1", "user2"));
        requestUserCouponDTO.setCouponPolicyId(1L);
        requestUserCouponDTO.setState(CouponState.LIVE);
        requestUserCouponDTO.setStartDate(LocalDate.now());
        requestUserCouponDTO.setEndDate(LocalDate.now().plusDays(30));
    }

    @Test
    void testUserCouponCreateSuccess() {
        // Given
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));

        // When
        boolean result = adminCouponService.userCouponCreate(requestUserCouponDTO);

        // Then
        assertTrue(result);
        verify(userCouponRepository, times(1)).saveAll(anyList());  // saveAll이 한 번 호출된 것을 확인
    }

    @Test
    void testUserCouponCreatePolicyNotFound() {
        // Given
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        boolean result = adminCouponService.userCouponCreate(requestUserCouponDTO);

        // Then
        assertFalse(result);  // 정책이 없으면 false 반환
        verify(userCouponRepository, never()).saveAll(anyList());  // saveAll이 호출되지 않음
    }

    @Test
    void testUserCouponCreateWithException() {
        // Given
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));
        doThrow(new DataIntegrityViolationException("Data integrity violation"))
                .when(userCouponRepository).saveAll(anyList());

        // When
        boolean result = adminCouponService.userCouponCreate(requestUserCouponDTO);

        // Then
        assertFalse(result);  // 예외가 발생하면 false 반환
        verify(userCouponRepository, times(1)).saveAll(anyList());  // saveAll이 호출된 것을 확인
    }
}
