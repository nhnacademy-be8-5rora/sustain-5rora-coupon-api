package store.aurora.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountRuleDTO {
    private Integer needCost;
    private Integer maxSale;
    private Integer salePercent;
    private Integer saleAmount;
}
