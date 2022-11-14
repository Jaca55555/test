package uz.maroqand.ecology.core.report;

import lombok.Data;
import uz.sspm.business.gov.core.constant.sys.DocumentOrderType;
import uz.sspm.business.gov.core.dto.DocumentOrderParams;

/**
 * Created by Utkirbek Boltaev on 19.06.2019.
 * (uz)
 * (ru)
 */
@Data
public class ReportClassifierExcelOrder implements DocumentOrderParams {

    private String dateBeginStr;
    private String dateEndStr;
    private Long regionId;
    private Long subRegionId;

    @Override
    public DocumentOrderType getType() {
        return DocumentOrderType.AppealReportClassification;
    }

}
