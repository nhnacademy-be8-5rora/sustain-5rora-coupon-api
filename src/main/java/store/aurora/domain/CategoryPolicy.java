package store.aurora.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "category_policy")
@Getter
@Setter
public class CategoryPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_coupon")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    private CouponPolicy policy;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

}
