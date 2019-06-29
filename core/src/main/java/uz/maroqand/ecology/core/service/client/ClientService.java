package uz.maroqand.ecology.core.service.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.Date;

public interface ClientService {

    Client getById(Integer id);

    Client createClient(LegalEntityDto legalEntityDto);

    Client createClient(IndividualDto individualDto);

    Client updateClient(Client client, LegalEntityDto legalEntityDto, User user);

    Client updateClient(Client client, IndividualDto individualDto, User user);

    Page<Client> findFiltered(
            ApplicantType type,
            String tin,
            String name,
            Integer opfId,
            String oked,
            Integer regionId,
            Integer subRegionId,
            Date beginDate,
            Date endDate,
            Pageable pageable
    );

}
