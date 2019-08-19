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
public class ForeignIndividualDto {

    private Integer id;

    //Ф.И.О.
    private String foreignIndividualName;

    private String foreignPassportSerialNumber;
    private Integer foreignCitizenshipId;

    private Integer foreignCountryId;
    private String foreignAddress;

    private String foreignPhone;
    private String foreignMobilePhone;
    private String foreignEmail;

    public ForeignIndividualDto(){}

    public ForeignIndividualDto(Client applicant){
        this.id = applicant.getId();
        this.foreignIndividualName = applicant.getName();

        this.foreignPassportSerialNumber = applicant.getPassportNumber();
        this.foreignCitizenshipId = applicant.getCitizenshipId();

        this.foreignCountryId = applicant.getCountryId();
        this.foreignAddress = applicant.getAddress();

        this.foreignPhone = applicant.getPhone();
        this.foreignMobilePhone = applicant.getMobilePhone();
        this.foreignEmail = applicant.getEmail();
    }

}
