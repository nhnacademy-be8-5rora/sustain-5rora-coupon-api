package store.aurora.dto.response;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UsedCouponDTO {
    private final String couponName;  //쿠폰 이름
    private final LocalDate usedDate; //쿠폰 사용한 날짜
    private final LocalDate startDate;
    private final LocalDate endDate;

    public UsedCouponDTO(String name, LocalDate usedDate, LocalDate startDate, LocalDate endDate) {
        this.couponName = name;
        this.usedDate = usedDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
