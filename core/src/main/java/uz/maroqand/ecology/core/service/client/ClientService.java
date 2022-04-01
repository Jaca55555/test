package uz.maroqand.ecology.core.service.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.dto.expertise.ForeignIndividualDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualEntrepreneurDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.user.User;

import java.util.Date;
import java.util.List;

public interface ClientService {

    Client getById(Integer id);

    Client getByTin(Integer tin);

    List<Client> getByListTin(Integer tin);
    List<Client> getByListPinfl(String pinfl);
    List<Client> getByListTinAndPinfl(Integer tin,String pinfl);

    Client saveLegalEntity(LegalEntityDto legalEntityDto, User user, String message);

    Client saveIndividual(IndividualDto individualDto, User user, String message);

    Client saveIndividualEntrepreneur(IndividualEntrepreneurDto individualEntrepreneurDto, User user, String message);

    Client saveForeignIndividual(ForeignIndividualDto foreignIndividualDto, User user, String message);

    Client saveForEdit(Client client);

    Client updateGnk(Client client);

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

    void clientView(Integer applicantId, Model model);

}
