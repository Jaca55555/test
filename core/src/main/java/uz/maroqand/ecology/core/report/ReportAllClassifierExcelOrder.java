package uz.maroqand.ecology.core.report;

import lombok.Data;
import uz.sspm.business.gov.core.constant.report.ReportType;
import uz.sspm.business.gov.core.constant.sys.DocumentOrderType;
import uz.sspm.business.gov.core.dto.DocumentOrderParams;

import java.util.Date;

@Data
public class ReportAllClassifierExcelOrder implements DocumentOrderParams {

    private Integer regionId;
    private Date dateBegin;
    private Date dateEnd;

    private ReportType reportType;
    @Override
    public DocumentOrderType getType() {
        return DocumentOrderType.AppealReportAllClassification;
    }
}
