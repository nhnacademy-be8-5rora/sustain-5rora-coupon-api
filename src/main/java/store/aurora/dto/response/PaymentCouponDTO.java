package store.aurora.dto.response;


/**
 * @param id          사용자 쿠폰 ID
 * @param couponName  쿠폰명
 * @param needCost    필요한 가격
 * @param maxSale     최대 할인가
 * @param salePercent 할인률
 * @param saleAmount  할인값
 */

public record PaymentCouponDTO(Long id, String couponName, Integer needCost, Integer maxSale, Integer saleAmount,
                               Integer salePercent) {
}
