package uz.maroqand.ecology.core.dto.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.util.Common;

/**
 * Created by Utkirbek Boltaev on 12.06.2019.
 * (uz)
 * (ru)
 */
@Data
public class IndividualEntrepreneurDto {

    private Integer id;

    private String individualPinfl;
    //Ф.И.О.
    private String individualEntrepreneurName;
    private Integer individualEntrepreneurTin;

    private String entrepreneurPassportSerial;
    private String entrepreneurPassportNumber;
    private String entrepreneurPassportDateOfIssue;
    private String entrepreneurPassportDateOfExpiry;
    private String entrepreneurPassportIssuedBy;

    private Integer entrepreneurCountryId;
    private Integer entrepreneurRegionId;
    private Integer entrepreneurSubRegionId;
    private String entrepreneurAddress;

    private String entrepreneurPhone;
    private String entrepreneurMobilePhone;
    private String entrepreneurEmail;

    public IndividualEntrepreneurDto(){}

    public IndividualEntrepreneurDto(Client applicant){
        this.id = applicant.getId();
        this.individualEntrepreneurName = applicant.getName();
        this.individualEntrepreneurTin = applicant.getTin();

        this.entrepreneurPassportSerial = applicant.getPassportSerial();
        this.entrepreneurPassportNumber = applicant.getPassportNumber();
        this.entrepreneurPassportDateOfIssue = applicant.getPassportDateOfIssue()!=null?Common.uzbekistanDateFormat.format(applicant.getPassportDateOfIssue()):"";
        this.entrepreneurPassportDateOfExpiry = applicant.getPassportDateOfExpiry()!=null?Common.uzbekistanDateFormat.format(applicant.getPassportDateOfExpiry()):"";
        this.entrepreneurPassportIssuedBy = applicant.getPassportIssuedBy();

        this.entrepreneurCountryId = applicant.getCountryId();
        this.entrepreneurRegionId = applicant.getRegionId();
        this.entrepreneurSubRegionId = applicant.getSubRegionId();
        this.entrepreneurAddress = applicant.getAddress();

        this.entrepreneurPhone = applicant.getPhone();
        this.entrepreneurMobilePhone = applicant.getMobilePhone();
        this.entrepreneurEmail = applicant.getEmail();
    }

}
