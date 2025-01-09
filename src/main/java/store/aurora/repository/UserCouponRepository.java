package store.aurora.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import store.aurora.domain.CouponPolicy;
import store.aurora.domain.CouponState;
import store.aurora.domain.UserCoupon;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    @Query("SELECT u FROM UserCoupon u WHERE u.userId = :userId")
    List<UserCoupon> findByUserId(@Param("userId") String userId);

    //쿠폰 정책 리스트 출력
    List<CouponPolicy> findCouponPolicyByCouponIdIn(List<Long> couponIds);

    //관리자가 특정 사용자 ID 리스트에 해당하는 UserCoupon들의 couponState/endDate/policyId 업데이트
    @Modifying
    @Query("UPDATE UserCoupon uc SET uc.couponState = :couponState WHERE uc.userId IN :userIds")
    void updateCouponStateByUserIds(@Param("couponState") CouponState couponState,
                                    @Param("userIds") List<String> userIds);
    @Modifying
    @Query("UPDATE UserCoupon uc SET uc.endDate = :endDate WHERE uc.userId IN :userIds")
    void updateCouponEndDateByUserIds(@Param("endDate") LocalDate endDate,
                                      @Param("userIds") List<String> userIds);

    @Modifying
    @Query("UPDATE UserCoupon uc SET uc.policy = (SELECT p FROM CouponPolicy p WHERE p.id = :policyId) " +
            "WHERE uc.userId IN :userIds AND EXISTS (SELECT 1 FROM CouponPolicy p WHERE p.id = :policyId)")
    void updateCouponPolicyByUserIds(@Param("policyId") Long policyId,  //해당 policyId을 가진 policy 없을시 퀴리 적용 안되게함
                                     @Param("userIds") List<String> userIds);

    //timeout 이 된 사용자 쿠폰 상태 변경(live -> timeout)
    @Modifying
    @Query("UPDATE UserCoupon u SET u.couponState = 'timeout', " +
            "    u.usedDate = CURRENT_DATE " +
            "WHERE u.endDate < CURRENT_DATE AND u.couponState = 'live'")
    void updateExpiredCoupons();

    //endDate에서 90일이 지난  userCoupon들 삭제
    @Modifying
    @Query("DELETE FROM UserCoupon u " +
            "WHERE u.endDate < :date")
    void deleteExpiredCoupons(
            @Param("date") LocalDate date);

    List<UserCoupon> findByUserIdIn(List<String> userIds);

    // userId, policyId, couponState에 맞는 데이터가 존재하는지 확인
    boolean existsByUserIdAndPolicyId(String userId, Long policyId);

    @Query("SELECT cp " +
            "FROM UserCoupon uc " +
            "JOIN uc.policy cp " +
            "LEFT JOIN cp.bookPolicies bp " +
            "LEFT JOIN cp.categoryPolicies cpCat " +
            "WHERE uc.userId = :userId " +
            "AND uc.couponState = 'LIVE' " +
            "AND ( " +
            "  (bp.bookId IS NULL OR bp.bookId IN :bookIds) " +
            "  OR " +
            "  (cpCat.categoryId IS NULL OR cpCat.categoryId IN :categoryIds) " +
            ") " +
            "AND (cp.discountRule.needCost IS NULL OR cp.discountRule.needCost <= :priceMap)")
    List<CouponPolicy> findAvailableCouponsForBatch(@Param("userId") String userId,
                                                    @Param("bookIds") List<Long> bookIds,
                                                    @Param("categoryIds") List<Long> categoryIds,
                                                    @Param("priceMap") Map<Long, Integer> priceMap);
}