package uz.maroqand.ecology.core.service.client;

import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;

public interface ClientService {

    Client getById(Integer id);

    Client createClient(LegalEntityDto legalEntityDto);

    Client createClient(IndividualDto individualDto);

    Client updateClient(Client client, LegalEntityDto legalEntityDto);

    Client updateClient(Client client, IndividualDto individualDto);

}
