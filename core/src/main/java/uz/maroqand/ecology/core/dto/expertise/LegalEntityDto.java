package uz.maroqand.ecology.core.dto.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.user.User;

/**
 * Created by Utkirbek Boltaev on 12.06.2019.
 * (uz)
 * (ru)
 */
@Data
public class LegalEntityDto {

    private Integer id;
    private Integer legalEntityTin;
    private String legalEntityName;

    private Integer legalEntityOpfId;
    private String directorFullName;
    private String oked;

    private Integer legalEntityRegionId;
    private Integer legalEntitySubRegionId;
    private String legalEntityAddress;

    private String legalEntityPhone;
    private String legalEntityMobilePhone;
    private String legalEntityEmail;

    private String legalEntityMfo;
    private String legalEntityBankName;
    private String legalEntityBankAccount;

    public LegalEntityDto(){}

    public LegalEntityDto(Client applicant, User user){
        this.id = applicant.getId();
        this.legalEntityTin = user.getLeTin();
        this.legalEntityName = applicant.getName();

        this.legalEntityOpfId = applicant.getOpfId();
        this.directorFullName = applicant.getDirectorFullName();
        this.oked = applicant.getOked();

        this.legalEntityRegionId = applicant.getRegionId();
        this.legalEntitySubRegionId = applicant.getSubRegionId();
        this.legalEntityAddress = applicant.getAddress();

        this.legalEntityPhone = applicant.getPhone();
        this.legalEntityMobilePhone = applicant.getMobilePhone();
        this.legalEntityEmail = applicant.getEmail();

        this.legalEntityMfo = applicant.getMfo();
        this.legalEntityBankName = applicant.getBankName();
        this.legalEntityBankAccount = applicant.getBankAccount();
    }

}
