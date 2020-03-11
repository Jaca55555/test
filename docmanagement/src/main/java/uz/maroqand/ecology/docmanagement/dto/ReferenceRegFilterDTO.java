package uz.maroqand.ecology.docmanagement.dto;

import lombok.Data;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.Date;

@Data
public class ReferenceRegFilterDTO {
    private Integer tabFilter;
    private Integer documentOrganizationId;
    private String docRegNumber;
    private String registrationNumber;
    private String dateBeginStr;
    private String dateEndStr;
    private Integer controlForm;
    private Integer documentViewId;
    private Integer managerId;
    private String taskContent;
    private Integer performerId;
    private Boolean insidePurpose;
    private String address;
    private String receiverId;
    private String type;
    private String status;
    private String fullName;
    private String content;
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

    public void initNull() {
        this.tabFilter = null;
        this.documentOrganizationId = null;
        this.docRegNumber = null;
        this.registrationNumber = null;
        this.dateBeginStr = null;
        this.dateEndStr = null;
        this.controlForm = null;
        this.documentViewId = null;
        this.managerId = null;
        this.taskContent = null;
        this.performerId = null;
        this.receiverId = null;
        this.type = null;
        this.status = null;
        this.dueDateBeginStr = null;
        this.dueDateEndStr = null;
    }
}
