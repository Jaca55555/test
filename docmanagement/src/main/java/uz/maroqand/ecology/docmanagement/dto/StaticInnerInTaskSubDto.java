package uz.maroqand.ecology.docmanagement.dto;

import lombok.Data;

@Data
public class StaticInnerInTaskSubDto {
    private Integer allCount;//barcha ichki xatlar
    private Integer newCount;//yangi
    private Integer inProgressCount;//jarayondagilar
    private Integer lessDeadlineCount;//muddati yaqinlaganlar
    private Integer greaterDeadlineCount;//muddati kechikkanlar
    private Integer checkingCount;//ijro etilganlar

    public StaticInnerInTaskSubDto() {
        this.allCount = 0;
        this.newCount = 0;
        this.inProgressCount = 0;
        this.lessDeadlineCount = 0;
        this.greaterDeadlineCount = 0;
        this.checkingCount = 0;
    }
}
