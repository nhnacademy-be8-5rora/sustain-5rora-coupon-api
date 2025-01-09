package store.aurora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.domain.CouponPolicy;

@Repository
public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {

}

