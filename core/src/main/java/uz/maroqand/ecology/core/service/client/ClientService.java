package uz.maroqand.ecology.core.service.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.dto.expertise.ForeignIndividualDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualEntrepreneurDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.Date;

public interface ClientService {

    Client getById(Integer id);

    Client saveLegalEntity(LegalEntityDto legalEntityDto, User user, String message);

    Client saveIndividual(IndividualDto individualDto, User user, String message);

    Client saveIndividualEntrepreneur(IndividualEntrepreneurDto individualEntrepreneurDto, User user, String message);

    Client saveForeignIndividual(ForeignIndividualDto foreignIndividualDto, User user, String message);

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
