package uz.maroqand.ecology.docmanagement.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResolutionDTO {
    private String organizationName;
    private String controlUser;
    private String executeFormName;
    private String content;
    private String performers;
    private String managerName;
    private String managerPosition;
    private String registrationNumber;
    private String registrationDate;
    private String dueDate;

    public ResolutionDTO() {
        this.organizationName = "";
        this.controlUser = "";
        this.executeFormName = "";
        this.content = "";
        this.performers = "";
        this.managerName = "";
        this.managerPosition = "";
        this.registrationNumber = "";
        this.registrationDate = "";
        this.dueDate = "";
    }
}
