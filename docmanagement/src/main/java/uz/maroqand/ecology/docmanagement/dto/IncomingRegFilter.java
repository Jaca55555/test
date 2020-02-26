package uz.maroqand.ecology.docmanagement.dto;

import lombok.Data;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 16.02.2020.
 * (uz)
 */
@Data
public class IncomingRegFilter {
    private Integer tabFilter;

    private Integer documentOrganizationId;
    private String docRegNumber;
    private String registrationNumber;
    private String dateBeginStr;
    private String dateEndStr;
    private Integer controlForm;
    private Integer documentViewId;
    private String content;
    private Integer managerId;
    private String taskContent;
    private Integer performerId;

    private String receiverId;
    private String type;
    private String status;

    private String dueDateBeginStr;
    private String dueDateEndStr;

    public Date getDateBegin() {
        return (dateBeginStr != null) ? DateParser.TryParse(dateBeginStr, Common.uzbekistanDateFormat) : null;
    }

    public Date getDateEnd() {
        return (dateEndStr != null) ? DateParser.TryParse(dateEndStr, Common.uzbekistanDateFormat) : null;
    }

    public Date getDueDateBegin() {
        return (dueDateBeginStr != null) ? DateParser.TryParse(dueDateBeginStr, Common.uzbekistanDateFormat) : null;
    }

    public Date getDueDateEnd() {
        return (dueDateEndStr != null) ? DateParser.TryParse(dueDateEndStr, Common.uzbekistanDateFormat) : null;
    }
}
