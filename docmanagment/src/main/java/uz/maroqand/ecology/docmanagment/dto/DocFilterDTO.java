package uz.maroqand.ecology.docmanagment.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import uz.maroqand.ecology.docmanagment.constant.DocumentSubType;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 24.12.2019
 */

@Data
public class DocFilterDTO {
    private String correspondent;
    private Integer documentId;
    private String registrationNumber;
    private String regsitrationDateBegin;
    private String regsitrationDateEnd;
    private String controlCard;
    private Integer documentType;
    private String correspondentType;
    private String content;
    private String chief;
    private String executors;
    private String resolution;
    private String executePath;
    private Integer executeStatus;
    private String executeDateBegin;
    private String executeDateEnd;
    private Integer executeControlStatus;
    private Boolean insidePurposeStatus;
    private Integer coexecutorStatus;
    private String replies;

    public String getContent() {
        return StringUtils.trimToNull(content);
    }

    public String getRegistrationNumber() {
        return StringUtils.trimToNull(registrationNumber);
    }

}
