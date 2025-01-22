package store.aurora.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import store.aurora.domain.SaleType;

@Getter
@Setter
public class RequestCouponPolicyDTO {
    @NotNull private String policyName;
    @NotNull private SaleType saleType;
    private AddPolicyDTO addPolicyDTO;
    @NotNull private DiscountRuleDTO discountRuleDTO;
}

