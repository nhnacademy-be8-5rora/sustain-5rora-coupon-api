package store.aurora.dto;

import lombok.Data;

@Data
public class PaymentCouponDTO {
    private Long id;        //사용자 쿠폰 ID
    private String couponName;  //쿠폰명
    private Integer needCost;   //필요한 가격
    private Integer maxSale;    //최대 할인가
    private Integer salePercent;    //할인률
    private Integer saleAmount;     //할인값

    public PaymentCouponDTO(Long id, String couponName, Integer needCost
            , Integer maxSale, Integer saleAmount, Integer salePercent) {
        this.id = id;
        this.couponName = couponName;
        this.needCost = needCost;
        this.maxSale = maxSale;
        this.saleAmount = saleAmount;
        this.salePercent = salePercent;
    }
}
