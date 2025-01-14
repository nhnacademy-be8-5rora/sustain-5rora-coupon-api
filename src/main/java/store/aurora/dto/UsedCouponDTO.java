package store.aurora.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UsedCouponDTO {
    private String couponName;  //쿠폰 이름
    private LocalDate usedDate; //쿠폰 사용한 날짜
    private LocalDate startDate;
    private LocalDate endDate;

    public UsedCouponDTO(String name, LocalDate usedDate, LocalDate endDate, LocalDate startDate) {
        this.couponName = name;
        this.usedDate = usedDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
