package uz.maroqand.ecology.core.constant.order;

import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.Category;

import java.util.Date;
@Data
public class RegApplicationExcelOrder implements DocumentOrderParams {
    private Date beginDate;

    private Date endDate;
    private Integer reviewId;
    private Category category;

    @Override
    public DocumentOrderType getType() {
        return DocumentOrderType.RegApplication;
    }
}
