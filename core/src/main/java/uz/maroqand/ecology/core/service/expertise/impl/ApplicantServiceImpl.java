package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.expertise.Applicant;
import uz.maroqand.ecology.core.repository.expertise.ApplicantRepository;
import uz.maroqand.ecology.core.service.expertise.ApplicantService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.Date;

@Service
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantRepository applicantRepository;

    @Autowired
    public ApplicantServiceImpl(ApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }

    @Override
    public Applicant createApplicant(LegalEntityDto legalEntityDto) {
        Applicant applicant = new Applicant();
        applicant.setType(ApplicantType.LegalEntity);
        applicant.setTin(legalEntityDto.getLegalEntityTin());
        applicant.setName(legalEntityDto.getLegalEntityName());
        applicant.setOpfId(legalEntityDto.getLegalEntityOpfId());
        applicant.setDirectorFullName(legalEntityDto.getDirectorFullName());
        applicant.setOked(legalEntityDto.getOked());
        applicant.setRegionId(legalEntityDto.getLegalEntityRegionId());
        applicant.setSubRegionId(legalEntityDto.getLegalEntitySubRegionId());
        applicant.setAddress(legalEntityDto.getLegalEntityAddress());
        applicant.setPhone(legalEntityDto.getLegalEntityPhone());
        applicant.setMobilePhone(legalEntityDto.getLegalEntityMobilePhone());
        applicant.setEmail(legalEntityDto.getLegalEntityEmail());
        applicant.setMfo(legalEntityDto.getLegalEntityMfo());
        applicant.setBankName(legalEntityDto.getLegalEntityBankName());
        applicant.setBankAccount(legalEntityDto.getLegalEntityBankAccount());
        applicant = applicantRepository.save(applicant);
        return applicant;
    }

    @Override
    public Applicant createApplicant(IndividualDto individualDto) {
        Date passportDateOfIssue=null;
        Date passportDateOfExpiry=null;

        try {
            passportDateOfIssue = DateParser.TryParse(individualDto.getPassportDateOfIssue(), Common.uzbekistanDateFormat);
        }catch (Exception e){}

        try {
            passportDateOfExpiry = DateParser.TryParse(individualDto.getPassportDateOfExpiry(), Common.uzbekistanDateFormat);
        }catch (Exception e){}
        Applicant applicant = new Applicant();
        applicant.setType(ApplicantType.Individual);
        applicant.setName(individualDto.getIndividualName());
        applicant.setPassportSerial(individualDto.getPassportSerial());
        applicant.setPassportNumber(individualDto.getPassportNumber());
        applicant.setPassportDateOfIssue(passportDateOfIssue);
        applicant.setPassportDateOfExpiry(passportDateOfExpiry);
        applicant.setPassportIssuedBy(individualDto.getPassportIssuedBy());
        applicant.setRegionId(individualDto.getIndividualRegionId());
        applicant.setSubRegionId(individualDto.getIndividualSubRegionId());
        applicant.setAddress(individualDto.getIndividualAddress());
        applicant.setPhone(individualDto.getIndividualPhone());
        applicant.setMobilePhone(individualDto.getIndividualMobilePhone());
        applicant.setEmail(individualDto.getIndividualEmail());
        applicant = applicantRepository.save(applicant);
        return applicant;
    }

    @Override
    public Applicant updateApplicant(Applicant applicant, LegalEntityDto legalEntityDto) {
        applicant.setType(ApplicantType.LegalEntity);
        applicant.setTin(legalEntityDto.getLegalEntityTin());
        applicant.setName(legalEntityDto.getLegalEntityName());
        applicant.setOpfId(legalEntityDto.getLegalEntityOpfId());
        applicant.setDirectorFullName(legalEntityDto.getDirectorFullName());
        applicant.setOked(legalEntityDto.getOked());
        applicant.setRegionId(legalEntityDto.getLegalEntityRegionId());
        applicant.setSubRegionId(legalEntityDto.getLegalEntitySubRegionId());
        applicant.setAddress(legalEntityDto.getLegalEntityAddress());
        applicant.setPhone(legalEntityDto.getLegalEntityPhone());
        applicant.setMobilePhone(legalEntityDto.getLegalEntityMobilePhone());
        applicant.setEmail(legalEntityDto.getLegalEntityEmail());
        applicant.setMfo(legalEntityDto.getLegalEntityMfo());
        applicant.setBankName(legalEntityDto.getLegalEntityBankName());
        applicant.setBankAccount(legalEntityDto.getLegalEntityBankAccount());
        applicant = applicantRepository.save(applicant);
        return applicant;
    }

    @Override
    public Applicant updateApplicant(Applicant applicant, IndividualDto individualDto) {
        Date passportDateOfIssue=null;
        Date passportDateOfExpiry=null;

        try {
            passportDateOfIssue = DateParser.TryParse(individualDto.getPassportDateOfIssue(), Common.uzbekistanDateFormat);
        }catch (Exception e){}

        try {
            passportDateOfExpiry = DateParser.TryParse(individualDto.getPassportDateOfExpiry(), Common.uzbekistanDateFormat);
        }catch (Exception e){}

        applicant.setType(ApplicantType.Individual);
        applicant.setName(individualDto.getIndividualName());
        applicant.setPassportSerial(individualDto.getPassportSerial());
        applicant.setPassportNumber(individualDto.getPassportNumber());
        applicant.setPassportDateOfIssue(passportDateOfIssue);
        applicant.setPassportDateOfExpiry(passportDateOfExpiry);
        applicant.setPassportIssuedBy(individualDto.getPassportIssuedBy());
        applicant.setRegionId(individualDto.getIndividualRegionId());
        applicant.setSubRegionId(individualDto.getIndividualSubRegionId());
        applicant.setAddress(individualDto.getIndividualAddress());
        applicant.setPhone(individualDto.getIndividualPhone());
        applicant.setMobilePhone(individualDto.getIndividualMobilePhone());
        applicant.setEmail(individualDto.getIndividualEmail());
        applicant = applicantRepository.save(applicant);
        return applicant;
    }
}
