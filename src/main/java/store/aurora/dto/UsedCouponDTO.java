package store.aurora.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UsedCouponDTO {
    private String couponName;  //쿠폰 이름
    private LocalDate usedDate; //쿠폰 사용한 날짜
    private LocalDate startDate;
    private LocalDate endDate;

    public UsedCouponDTO(String name, LocalDate usedDate, LocalDate startDate, LocalDate endDate) {
        this.couponName = name;
        this.usedDate = usedDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
