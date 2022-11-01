package uz.maroqand.ecology.core.constant.order;

import lombok.Data;

import java.util.Date;
@Data
public class RegApplicationExcelOrder implements DocumentOrderParams {
    private Date beginDate;

    private Date endDate;

    @Override
    public DocumentOrderType getType() {
        return DocumentOrderType.RegApplication;
    }
}
