package uz.maroqand.ecology.core.report;

import lombok.Data;
import uz.sspm.business.gov.core.constant.report.ReportType;
import uz.sspm.business.gov.core.constant.sys.DocumentOrderType;
import uz.sspm.business.gov.core.dto.DocumentOrderParams;

import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 03.07.2019.
 * (uz)
 * (ru)
 */
@Data
public class ReportExcelOrder  implements DocumentOrderParams {

    private Integer receiverId; //Appeal.receiverId
    private Integer organizationId; //Task.executiveOrganizationId

    private Date dateBegin;
    private Date dateEnd;

    private Integer parentId;
    private Integer creator; //0-hammasi,1-ishchi guruh dan tashqari,2-faqat ishchi guruh

    private ReportType reportType;

//    private List<ReportType> reportTypes;

    @Override
    public DocumentOrderType getType() {
        return DocumentOrderType.AppealReport;
    }

}

