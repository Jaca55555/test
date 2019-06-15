package uz.maroqand.ecology.core.service.client;

import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;

public interface ClientService {

    Client createApplicant(LegalEntityDto legalEntityDto);
    Client createApplicant(IndividualDto individualDto);

    Client updateApplicant(Client applicant, LegalEntityDto legalEntityDto);
    Client updateApplicant(Client applicant, IndividualDto individualDto);
}
