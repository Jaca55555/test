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
public class IndividualDto {

    private Integer id;

    private String individualPinfl;
    private Integer individualTin;
    //Ф.И.О.
    private String individualName;

    private String passportSerial;
    private String passportNumber;
    private String passportDateOfIssue;
    private String passportDateOfExpiry;
    private String passportIssuedBy;

    private Integer individualRegionId;
    private Integer individualSubRegionId;
    private String individualAddress;

    private String individualPhone;
    private String individualMobilePhone;
    private String individualEmail;

    public IndividualDto(){}

    public IndividualDto(Client applicant){
        this.id = applicant.getId();
        this.individualName = applicant.getName();
        this.individualPinfl = applicant.getPinfl();
        this.individualTin = applicant.getTin();

        this.passportSerial = applicant.getPassportSerial();
        this.passportNumber = applicant.getPassportNumber();
        this.passportDateOfIssue = applicant.getPassportDateOfIssue()!=null?Common.uzbekistanDateFormat.format(applicant.getPassportDateOfIssue()):"";
        this.passportDateOfExpiry = applicant.getPassportDateOfExpiry()!=null?Common.uzbekistanDateFormat.format(applicant.getPassportDateOfExpiry()):"";
        this.passportIssuedBy = applicant.getPassportIssuedBy();

        this.individualRegionId = applicant.getRegionId();
        this.individualSubRegionId = applicant.getSubRegionId();
        this.individualAddress = applicant.getAddress();

        this.individualPhone = applicant.getPhone();
        this.individualMobilePhone = applicant.getMobilePhone();
        this.individualEmail = applicant.getEmail();
    }

}
