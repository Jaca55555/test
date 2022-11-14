package uz.maroqand.ecology.core.report;

import lombok.Data;
import uz.sspm.business.gov.core.constant.report.ReportType;
import uz.sspm.business.gov.core.constant.sys.DocumentOrderType;
import uz.sspm.business.gov.core.dto.DocumentOrderParams;

import java.util.Date;
import java.util.Set;

@Data
public class ReportOrganizationAndSoato implements DocumentOrderParams {

    private Date dateBegin;
    private Date dateEnd;
    private Long regionId;
    private Integer organizationId;
    private Set<Integer> classifierIds;
    private String locale;

    private Integer creatorId; //0-hammasi,1-ishchi guruh dan tashqari,2-faqat ishchi guruh

    private ReportType reportType;
    @Override
    public DocumentOrderType getType() {
        return DocumentOrderType.AppealReportOrganizationAndSoato;
    }
}
