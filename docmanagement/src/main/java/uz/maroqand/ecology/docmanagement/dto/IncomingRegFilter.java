package uz.maroqand.ecology.docmanagement.dto;

import lombok.Data;

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

    private String receiverId;
    private String type;
    private String status;

    private String dueDateBeginStr;
    private String dueDateEndStr;

}