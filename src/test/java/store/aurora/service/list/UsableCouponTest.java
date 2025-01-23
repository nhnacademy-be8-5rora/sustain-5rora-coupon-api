package store.aurora.service.list;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import store.aurora.domain.*;
import store.aurora.dto.ProductInfoDTO;
import store.aurora.dto.response.PaymentCouponDTO;
import store.aurora.mapper.UserCouponMapper;
import store.aurora.repository.UserCouponRepository;
import store.aurora.service.CouponListService;

import java.util.*;
import java.time.LocalDate;

class UsableCouponTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private UserCouponMapper userCouponMapper;

    @InjectMocks
    private CouponListService couponListService;

    private UserCoupon userCoupon;
    private ProductInfoDTO productInfoDTO;
    private List<ProductInfoDTO> productInfoDTOList;
    private CouponPolicy couponPolicy;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up DiscountRule
        DiscountRule discountRule = new DiscountRule();
        discountRule.setNeedCost(1000);
        discountRule.setMaxSale(500);
        discountRule.setSaleAmount(100);
        discountRule.setSalePercent(10);

        // Set up CouponPolicy
        couponPolicy = new CouponPolicy();
        couponPolicy.setId(1L);
        couponPolicy.setName("Summer Sale");
        couponPolicy.setSaleType(SaleType.PERCENT);
        couponPolicy.setDiscountRule(discountRule);

        // Set up UserCoupon
        userCoupon = new UserCoupon();
        userCoupon.setCouponId(1L);
        userCoupon.setPolicy(couponPolicy);
        userCoupon.setCouponState(CouponState.LIVE);
        userCoupon.setStartDate(LocalDate.now());
        userCoupon.setUserId("user123");

        // Set up ProductInfoDTO
        productInfoDTO = new ProductInfoDTO();
        productInfoDTO.setBookId(1L);
        productInfoDTO.setCategoryIds(Arrays.asList(1L, 2L));
        productInfoDTO.setPrice(1000);

        // Prepare the list of ProductInfoDTO
        productInfoDTOList = new ArrayList<>();
        productInfoDTOList.add(productInfoDTO);
    }

    @Test
    void testGetCouponListByCategory() {
        // Mock the repository to return a list of UserCoupons
        List<UserCoupon> userCoupons = Collections.singletonList(userCoupon);
        when(userCouponRepository.findByUserId("user123")).thenReturn(userCoupons);

        // Call the method under test
        Map<Long, List<PaymentCouponDTO>> result = couponListService.getCouponListByCategory("user123", productInfoDTOList);

        // Verify the result
        assertNotNull(result);
        assertTrue(result.containsKey(1L));  // bookId
        assertEquals(1, result.get(1L).size()); // Should contain one coupon

        PaymentCouponDTO paymentCouponDTO = result.get(1L).getFirst();
        assertEquals(1L, paymentCouponDTO.id());
        assertEquals("Summer Sale", paymentCouponDTO.couponName());
        assertEquals(1000, paymentCouponDTO.needCost());
        assertEquals(500, paymentCouponDTO.maxSale());
        assertEquals(100, paymentCouponDTO.saleAmount());
        assertEquals(100, paymentCouponDTO.salePercent());
    }

    @Test
    void testIsCouponAvailableForProduct_ValidBookPolicy() {
        // Set up a valid BookPolicy that matches the product's bookId
        BookPolicy bookPolicy = new BookPolicy();
        bookPolicy.setBookId(1L); // Matches the product's bookId
        couponPolicy.setBookPolicies(List.of(bookPolicy));

        // Call the method under test
        boolean result = couponListService.isCouponAvailableForProduct(couponPolicy, productInfoDTO);

        // Verify that the coupon is available for the product
        assertTrue(result);
    }

    @Test
    void testIsCouponAvailableForProduct_InvalidBookPolicy() {
        // Set up a BookPolicy with a different bookId that doesn't match the product's bookId
        BookPolicy bookPolicy = new BookPolicy();
        bookPolicy.setBookId(2L); // Does not match the product's bookId
        couponPolicy.setBookPolicies(List.of(bookPolicy));

        // Call the method under test
        boolean result = couponListService.isCouponAvailableForProduct(couponPolicy, productInfoDTO);

        // Verify that the coupon is not available for the product
        assertFalse(result);
    }


    @Test
    void testIsCouponAvailableForProduct_InvalidCategoryPolicy() {
        // Set up a CategoryPolicy with a different categoryId that doesn't match the product's categoryId
        CategoryPolicy categoryPolicy = new CategoryPolicy();
        categoryPolicy.setCategoryId(3L); // Does not match the product's categoryId
        couponPolicy.setCategoryPolicies(List.of(categoryPolicy));

        // Call the method under test
        boolean result = couponListService.isCouponAvailableForProduct(couponPolicy, productInfoDTO);

        // Verify that the coupon is not available for the product
        assertFalse(result);
    }
}