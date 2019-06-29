package uz.maroqand.ecology.core.service.client.impl;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.client.ClientRepository;
import uz.maroqand.ecology.core.service.client.ClientAuditService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientAuditService clientAuditService;
    private final Gson gson;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, ClientAuditService clientAuditService, Gson gson) {
        this.clientRepository = clientRepository;
        this.clientAuditService = clientAuditService;
        this.gson = gson;
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
    public Client updateClient(Client client, LegalEntityDto legalEntityDto, User user) {
        String before = gson.toJson(client);

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

        client.setUpdateAt(new Date());
        client.setUpdateById(user.getId());
        client = clientRepository.save(client);

        clientAuditService.create(client.getId(),before,gson.toJson(client),"",user.getId(),user.getUserAdditionalId());
        return client;
    }

    @Override
    public Client updateClient(Client client, IndividualDto individualDto, User user) {
        String before = gson.toJson(client);
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

        client.setUpdateAt(new Date());
        client.setUpdateById(user.getId());
        client = clientRepository.save(client);

        clientAuditService.create(client.getId(),before,gson.toJson(client),"",user.getId(),user.getUserAdditionalId());
        return client;
    }

    @Override
    public Page<Client> findFiltered(
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
    ){
        return clientRepository.findAll(
                getFilteringSpecification(type, tin, name, opfId, oked, regionId, subRegionId, beginDate, endDate),pageable);
    }

    private static Specification<Client> getFilteringSpecification(
            final ApplicantType type,
            final String tin,
            final String name,
            final Integer opfId,
            final String oked,
            final Integer regionId,
            final Integer subRegionId,
            final Date beginDate,
            final Date endDate
    ) {
        return new Specification<Client>() {
            @Override
            public Predicate toPredicate(Root<Client> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if(type!=null){
                    predicates.add(criteriaBuilder.equal(root.get("type"), type));
                }

                if(tin!=null){
                    predicates.add(criteriaBuilder.equal(root.get("tin"), tin));
                }

                if(type!=null){
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
                }

                if(opfId!=null){
                    predicates.add(criteriaBuilder.equal(root.get("opfId"), opfId));
                }

                if(oked!=null){
                    predicates.add(criteriaBuilder.like(root.get("oked"), "%" + oked + "%"));
                }

                if(regionId!=null){
                    predicates.add(criteriaBuilder.equal(root.get("regionId"), regionId));
                }

                if(subRegionId!=null){
                    predicates.add(criteriaBuilder.equal(root.get("subRegionId"), subRegionId));
                }

                if (beginDate != null && endDate == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt").as(Date.class), beginDate));
                }
                if (endDate != null && beginDate == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt").as(Date.class), endDate));
                }
                if (beginDate != null && endDate != null) {
                    predicates.add(criteriaBuilder.between(root.get("createdAt").as(Date.class), beginDate, endDate));
                }

                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

}
