package uz.maroqand.ecology.core.service.expertise;

import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.expertise.Applicant;

public interface ApplicantService {

    Applicant createApplicant(LegalEntityDto legalEntityDto);
    Applicant createApplicant(IndividualDto individualDto);

    Applicant updateApplicant(Applicant applicant, LegalEntityDto legalEntityDto);
    Applicant updateApplicant(Applicant applicant, IndividualDto individualDto);
}
