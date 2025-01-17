package store.aurora.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import store.aurora.domain.CouponState;
import store.aurora.domain.UserCoupon;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    List<UserCoupon> findByUserId(@Param("userId") String userId);

    //사용자 쿠폰 ID 을 통해서 UserCoupon 검색
    List<UserCoupon> findByCouponIdIn(List<Long> couponIds);

    //관리자가 특정 사용자 ID 리스트에 해당하는 UserCoupon들의 couponState/endDate/policyId 업데이트
    @Modifying
    @Query("UPDATE UserCoupon uc SET uc.couponState = :couponState WHERE uc.couponId IN :userCouponIds")
    void updateCouponStateByUserIds(@Param("couponState") CouponState couponState,
                                    @Param("userCouponIds") List<Long> userCouponIds);
    @Modifying
    @Query("UPDATE UserCoupon uc SET uc.endDate = :endDate WHERE uc.couponId IN :userCouponIds")
    void updateCouponEndDateByUserIds(@Param("endDate") LocalDate endDate,
                                      @Param("userCouponIds") List<Long> userCouponIds);

    @Modifying
    @Query("UPDATE UserCoupon uc SET uc.policy = (SELECT p FROM CouponPolicy p WHERE p.id = :policyId) " +
            "WHERE uc.couponId IN :userCouponIds AND EXISTS (SELECT 1 FROM CouponPolicy p WHERE p.id = :policyId)")
    void updateCouponPolicyByUserIds(@Param("policyId") Long policyId,  //해당 policyId을 가진 policy 없을시 퀴리 적용 안되게함
                                     @Param("userCouponIds") List<Long> userCouponIds);

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

    @Query("SELECT u FROM UserCoupon u WHERE u.userId = :userId AND u.couponState = :couponState")
    List<UserCoupon> findByUserIdAndState(@Param String userId,
                                          @Param CouponState couponState);

    @Modifying
    @Query("UPDATE UserCoupon u SET u.couponState = :couponState WHERE u.couponId = :userCouponId")
    void liveToUsedByUserCouponId(@Param Long userCouponId,
                                  @Param CouponState couponState);
}