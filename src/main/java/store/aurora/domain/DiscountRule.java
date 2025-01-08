package store.aurora.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "discount_rule")
@Data
public class DiscountRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_id")
    private Long id;

    @Column(name = "need_cost")
    private Integer needCost;   //필요한 가격

    @Column(name = "max_sale")
    private Integer maxSale;    //최대 할인가

    @Column(name = "sale_percent")
    private Integer salePercent;    //할인률

    @Column(name = "sale_amount")
    private Integer saleAmount;     //할인값
}
