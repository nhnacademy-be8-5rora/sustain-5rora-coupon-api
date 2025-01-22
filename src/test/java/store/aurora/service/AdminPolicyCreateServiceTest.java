package store.aurora.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.aurora.domain.*;
import store.aurora.dto.AddPolicyDTO;
import store.aurora.dto.DiscountRuleDTO;
import store.aurora.dto.RequestCouponPolicyDTO;
import store.aurora.repository.BookPolicyRepository;
import store.aurora.repository.CategoryPolicyRepository;
import store.aurora.repository.CouponPolicyRepository;
import store.aurora.repository.DiscountRuleRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.StreamSupport;


@ExtendWith(MockitoExtension.class)
class AdminPolicyCreateServiceTest {

    @Mock
    private DiscountRuleRepository discountRuleRepository;

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @Mock
    private CategoryPolicyRepository categoryPolicyRepository;

    @Mock
    private BookPolicyRepository bookPolicyRepository;

    @InjectMocks
    private AdminCouponService adminCouponService;

    @Test
    void testCouponPolicyCreate() {
        // Arrange
        DiscountRuleDTO discountRuleDTO = new DiscountRuleDTO();
        discountRuleDTO.setNeedCost(100);
        discountRuleDTO.setMaxSale(50);
        discountRuleDTO.setSalePercent(10);
        discountRuleDTO.setSaleAmount(20);

        AddPolicyDTO addPolicyDTO = new AddPolicyDTO();
        addPolicyDTO.setCategoryId(List.of(1L, 2L));
        addPolicyDTO.setBookId(List.of(3L, 4L));

        RequestCouponPolicyDTO requestCouponPolicyDTO = new RequestCouponPolicyDTO();
        requestCouponPolicyDTO.setPolicyName("Test Policy");
        requestCouponPolicyDTO.setSaleType(SaleType.AMOUNT);
        requestCouponPolicyDTO.setDiscountRuleDTO(discountRuleDTO);
        requestCouponPolicyDTO.setAddPolicyDTO(addPolicyDTO);

        DiscountRule mockDiscountRule = new DiscountRule();
        mockDiscountRule.setId(1L);

        CouponPolicy mockCouponPolicy = new CouponPolicy();
        mockCouponPolicy.setId(1L);
        mockCouponPolicy.setName("Test Policy");

        when(discountRuleRepository.save(any(DiscountRule.class))).thenReturn(mockDiscountRule);
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(mockCouponPolicy);

        // Act
        adminCouponService.couponPolicyCreate(requestCouponPolicyDTO, discountRuleDTO, addPolicyDTO);

        // Assert
        verify(discountRuleRepository, times(1)).save(any(DiscountRule.class));
        verify(couponPolicyRepository, times(1)).save(any(CouponPolicy.class));
        verify(categoryPolicyRepository, times(1)).saveAll(anyList());
        verify(bookPolicyRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testSaveCouponPolicy() {
        // Arrange
        DiscountRule discountRule = new DiscountRule();
        discountRule.setId(1L);

        RequestCouponPolicyDTO requestCouponPolicyDTO = new RequestCouponPolicyDTO();
        requestCouponPolicyDTO.setPolicyName("Policy Name");
        requestCouponPolicyDTO.setSaleType(SaleType.PERCENT);

        CouponPolicy couponPolicy = new CouponPolicy();

        // Act
        adminCouponService.saveCouponPolicy(couponPolicy, requestCouponPolicyDTO, discountRule);

        // Assert
        assertEquals("Policy Name", couponPolicy.getName());
        assertEquals(SaleType.PERCENT, couponPolicy.getSaleType());
        assertEquals(discountRule, couponPolicy.getDiscountRule());
        verify(couponPolicyRepository, times(1)).save(couponPolicy);
    }

    @Test
    void testSaveCategoryPolicies() {
        // Arrange
        CouponPolicy couponPolicy = new CouponPolicy();

        AddPolicyDTO addPolicyDTO = new AddPolicyDTO();
        addPolicyDTO.setCategoryId(List.of(1L, 2L));

        // Act
        adminCouponService.saveCategoryPolicies(couponPolicy, addPolicyDTO);

// Assert
        verify(categoryPolicyRepository, times(1)).saveAll(argThat(policies -> {
            List<CategoryPolicy> policyList = StreamSupport.stream(policies.spliterator(), false)
                    .toList();
            return policyList.size() == 2 &&
                    policyList.get(0).getCategoryId() == 1L &&
                    policyList.get(1).getCategoryId() == 2L;
        }));
    }

    @Test
    void testSaveBookPolicies() {
        // Arrange
        CouponPolicy couponPolicy = new CouponPolicy();

        AddPolicyDTO addPolicyDTO = new AddPolicyDTO();
        addPolicyDTO.setBookId(List.of(3L, 4L));

        // Act
        adminCouponService.saveBookPolicies(couponPolicy, addPolicyDTO);

        verify(bookPolicyRepository, times(1)).saveAll(argThat(policies -> {
            List<BookPolicy> policyList = StreamSupport.stream(policies.spliterator(), false)
                    .toList();
            return policyList.size() == 2 &&
                    policyList.get(0).getBookId() == 3L &&
                    policyList.get(1).getBookId() == 4L;
        }));
    }
}
