package store.aurora.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user_coupon")
@Getter
@Setter
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @ManyToOne  // Many UserCoupon -> One Policy
    @JoinColumn(name = "policy_id", nullable = false)  // 외래 키로 policy_id 지정
    private CouponPolicy policy;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_state")
    private CouponState couponState = CouponState.LIVE;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "change_period")
    private LocalDate usedDate;

}
