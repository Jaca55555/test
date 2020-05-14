package uz.maroqand.ecology.docmanagement.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;

import java.util.Set;

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
    private String registrationDateBegin;
    private String registrationDateEnd;
    private String controlCard;
    private Integer documentType;
    private String correspondentType;
    private String content;
    private Integer chief;
    private String executors;
    private String resolution;
    private String executePath;
    private Integer executeStatus;
    private String executeDateBegin;
    private String executeDateEnd;
    private Integer executeControlStatus;
    private Boolean insidePurposeStatus;
    private Integer coExecutorStatus;
    private String replies;
    private DocumentTypeEnum documentTypeEnum;
    private Set<DocumentStatus> documentStatuses;

    public String getContent() {
        return StringUtils.trimToNull(content);
    }

    public String getRegistrationNumber() {
        return StringUtils.trimToNull(registrationNumber);
    }

}
