package store.aurora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.aurora.domain.DiscountRule;

@Repository
public interface DisCountRuleRepository extends JpaRepository<DiscountRule, Long> {

}
