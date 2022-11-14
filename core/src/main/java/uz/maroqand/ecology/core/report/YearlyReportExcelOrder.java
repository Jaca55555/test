package uz.maroqand.ecology.core.report;

import lombok.Data;
import uz.sspm.business.gov.core.constant.report.YearlyReportType;
import uz.sspm.business.gov.core.constant.sys.DocumentOrderType;
import uz.sspm.business.gov.core.dto.DocumentOrderParams;

import java.util.List;

@Data
public class YearlyReportExcelOrder implements DocumentOrderParams {

    private Long parentId;
    private Integer year;
    private Integer creatorId;
    private YearlyReportType reportType;
    private List<Integer> periods;

    @Override
    public DocumentOrderType getType() {
        return DocumentOrderType.YearlyReport;
    }
}
