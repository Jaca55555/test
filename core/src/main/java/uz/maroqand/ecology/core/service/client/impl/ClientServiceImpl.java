package uz.maroqand.ecology.core.service.client.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.repository.client.ClientRepository;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import java.util.Date;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client getById(Integer id) {
        return clientRepository.getOne(id);
    }

    @Override
    public Client createClient(LegalEntityDto legalEntityDto) {
        Client client = new Client();
        client.setType(ApplicantType.LegalEntity);
        client.setTin(legalEntityDto.getLegalEntityTin());
        client.setName(legalEntityDto.getLegalEntityName());
        client.setOpfId(legalEntityDto.getLegalEntityOpfId());
        client.setDirectorFullName(legalEntityDto.getDirectorFullName());
        client.setOked(legalEntityDto.getOked());
        client.setRegionId(legalEntityDto.getLegalEntityRegionId());
        client.setSubRegionId(legalEntityDto.getLegalEntitySubRegionId());
        client.setAddress(legalEntityDto.getLegalEntityAddress());
        client.setPhone(legalEntityDto.getLegalEntityPhone());
        client.setMobilePhone(legalEntityDto.getLegalEntityMobilePhone());
        client.setEmail(legalEntityDto.getLegalEntityEmail());
        client.setMfo(legalEntityDto.getLegalEntityMfo());
        client.setBankName(legalEntityDto.getLegalEntityBankName());
        client.setBankAccount(legalEntityDto.getLegalEntityBankAccount());
        client = clientRepository.save(client);
        return client;
    }

    @Override
    public Client createClient(IndividualDto individualDto) {
        Date passportDateOfIssue = DateParser.TryParse(individualDto.getPassportDateOfIssue(), Common.uzbekistanDateFormat);
        Date passportDateOfExpiry = DateParser.TryParse(individualDto.getPassportDateOfExpiry(), Common.uzbekistanDateFormat);

        Client client = new Client();
        client.setType(ApplicantType.Individual);
        client.setName(individualDto.getIndividualName());
        client.setPassportSerial(individualDto.getPassportSerial());
        client.setPassportNumber(individualDto.getPassportNumber());
        client.setPassportDateOfIssue(passportDateOfIssue);
        client.setPassportDateOfExpiry(passportDateOfExpiry);
        client.setPassportIssuedBy(individualDto.getPassportIssuedBy());
        client.setRegionId(individualDto.getIndividualRegionId());
        client.setSubRegionId(individualDto.getIndividualSubRegionId());
        client.setAddress(individualDto.getIndividualAddress());
        client.setPhone(individualDto.getIndividualPhone());
        client.setMobilePhone(individualDto.getIndividualMobilePhone());
        client.setEmail(individualDto.getIndividualEmail());
        client = clientRepository.save(client);
        return client;
    }

    @Override
    public Client updateClient(Client client, LegalEntityDto legalEntityDto) {

        client.setType(ApplicantType.LegalEntity);
        client.setTin(legalEntityDto.getLegalEntityTin());
        client.setName(legalEntityDto.getLegalEntityName());
        client.setOpfId(legalEntityDto.getLegalEntityOpfId());
        client.setDirectorFullName(legalEntityDto.getDirectorFullName());
        client.setOked(legalEntityDto.getOked());
        client.setRegionId(legalEntityDto.getLegalEntityRegionId());
        client.setSubRegionId(legalEntityDto.getLegalEntitySubRegionId());
        client.setAddress(legalEntityDto.getLegalEntityAddress());
        client.setPhone(legalEntityDto.getLegalEntityPhone());
        client.setMobilePhone(legalEntityDto.getLegalEntityMobilePhone());
        client.setEmail(legalEntityDto.getLegalEntityEmail());
        client.setMfo(legalEntityDto.getLegalEntityMfo());
        client.setBankName(legalEntityDto.getLegalEntityBankName());
        client.setBankAccount(legalEntityDto.getLegalEntityBankAccount());
        client = clientRepository.save(client);
        return client;
    }

    @Override
    public Client updateClient(Client client, IndividualDto individualDto) {
        Date passportDateOfIssue = DateParser.TryParse(individualDto.getPassportDateOfIssue(), Common.uzbekistanDateFormat);
        Date passportDateOfExpiry = DateParser.TryParse(individualDto.getPassportDateOfExpiry(), Common.uzbekistanDateFormat);

        client.setType(ApplicantType.Individual);
        client.setName(individualDto.getIndividualName());
        client.setPassportSerial(individualDto.getPassportSerial());
        client.setPassportNumber(individualDto.getPassportNumber());
        client.setPassportDateOfIssue(passportDateOfIssue);
        client.setPassportDateOfExpiry(passportDateOfExpiry);
        client.setPassportIssuedBy(individualDto.getPassportIssuedBy());
        client.setRegionId(individualDto.getIndividualRegionId());
        client.setSubRegionId(individualDto.getIndividualSubRegionId());
        client.setAddress(individualDto.getIndividualAddress());
        client.setPhone(individualDto.getIndividualPhone());
        client.setMobilePhone(individualDto.getIndividualMobilePhone());
        client.setEmail(individualDto.getIndividualEmail());
        client = clientRepository.save(client);
        return client;
    }
}
