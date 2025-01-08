package store.aurora.dto;

import lombok.Data;

@Data
public class PaymentCouponDTO {
    private String couponName;
    private Integer needCost;   //필요한 가격
    private Integer maxSale;    //최대 할인가
    private Integer salePercent;    //할인률
    private Integer saleAmount;     //할인값

    public PaymentCouponDTO(String couponName, Integer needCost,
                            Integer maxSale, Integer salePercent, Integer saleAmount) {
        this.couponName = couponName;
        this.needCost = needCost;
        this.maxSale = maxSale;
        this.salePercent = salePercent;
        this.saleAmount = saleAmount;
    }
}
