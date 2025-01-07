package store.aurora.scheduler;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

@Service
public class BirthDayCouponService {

    /** 사용자의 생일을 기반으로 생일 쿠폰을 생성하는 메서드.
     * @param userId 사용자의 ID
     * @return 생일 쿠폰 유효 기간
     */
    public String generateBirthdayCoupon(Long userId) {
        // 사용자의 생일을 확인 (이 예시에서는 간단히 생일을 하드코딩)
        // 실제로는 DB에서 사용자의 생일을 조회하는 로직이 필요합니다.
        LocalDate userBirthday = getUserBirthday(userId); // 예시: 5월 1일

        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();

        // 사용자의 생일이 현재 월에 해당하는지 확인
        if (userBirthday.getMonthValue() == currentMonth) {
            // 생일 쿠폰 제공: 5월 1일부터 5월 31일까지
            LocalDate couponStartDate = LocalDate.of(today.getYear(), currentMonth, 1);
            LocalDate couponEndDate = couponStartDate.withDayOfMonth(couponStartDate.lengthOfMonth());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            return "생일 쿠폰 제공 기간: " + couponStartDate.format(formatter) + " ~ " + couponEndDate.format(formatter);
        } else {
            return "현재 생일이 아닙니다. 생일 쿠폰을 제공할 수 없습니다.";
        }
    }

    /**
     * 사용자 ID를 기반으로 생일을 조회하는 메서드.
     * @param userId 사용자 ID
     * @return 사용자 생일
     */
    private LocalDate getUserBirthday(Long userId) {
        // 실제로는 DB에서 사용자의 생일을 조회하는 로직을 구현합니다.
        // 예시로 5월 1일 생일을 반환
        return LocalDate.of(2000, Month.MAY, 1);
    }
}
