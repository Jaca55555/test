package uz.maroqand.ecology.core.report;

import lombok.Data;
import uz.sspm.business.gov.core.constant.sys.DocumentOrderType;
import uz.sspm.business.gov.core.dto.DocumentOrderParams;

import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 19.06.2019.
 * (uz)
 * (ru)
 */

@Data
public class ReportRegionExcelOrder implements DocumentOrderParams {

    private Date dateBegin;
    private Date dateEnd;
    private Long regionId;
    private Long subRegionId;

    @Override
    public DocumentOrderType getType() {
        return DocumentOrderType.AppealReportRegion;
    }

}
