package uz.maroqand.ecology.core.service.client.impl;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import uz.maroqand.ecology.core.constant.expertise.ApplicantType;
import uz.maroqand.ecology.core.dto.expertise.ForeignIndividualDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualDto;
import uz.maroqand.ecology.core.dto.expertise.IndividualEntrepreneurDto;
import uz.maroqand.ecology.core.dto.expertise.LegalEntityDto;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.repository.client.ClientRepository;
import uz.maroqand.ecology.core.service.client.ClientAuditService;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
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
    private final HelperService helperService;
    private final Gson gson;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, ClientAuditService clientAuditService, HelperService helperService, Gson gson) {
        this.clientRepository = clientRepository;
        this.clientAuditService = clientAuditService;
        this.helperService = helperService;
        this.gson = gson;
    }

    @Override
    public Client getById(Integer id) {
        return clientRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public Client getByTin(Integer tin) {
        return clientRepository.findTop1ByTinAndDeletedFalseOrderByIdDesc(tin);
    }

    @Override
    public List<Client> getByListTin(Integer tin) {
        return clientRepository.findByTinAndDeletedFalse(tin);
    }

    @Override
    public List<Client> getByListPinfl(String pinfl) {
        return clientRepository.findByPinflAndDeletedFalse(pinfl);
    }

    @Override
    public List<Client> getByListTinAndPinfl(Integer tin, String pinfl) {
        return clientRepository.findByTinAndPinflAndDeletedFalse(tin,pinfl);
    }

    //Yuridik shaxs
    @Override
    public Client saveLegalEntity(LegalEntityDto legalEntityDto, User user, String message) {

        Client client = clientRepository.findTop1ByTinAndDeletedFalseOrderByIdDesc(legalEntityDto.getLegalEntityTin());
        if(client==null){
            client = new Client();
            client.setCreatedAt(new Date());
            client.setCreatedById(user.getId());
        }
        client = clientRepository.save(client);
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

        clientAuditService.create(client.getId(),before,gson.toJson(client),message,user.getId(),user.getUserAdditionalId());
        return client;
    }

    //Jismoniy shaxs
    @Override
    public Client saveIndividual(IndividualDto individualDto, User user, String message) {

        Client client = clientRepository.findTop1ByPinflAndDeletedFalseOrderByIdDesc(individualDto.getIndividualPinfl());
        if(client==null){
            client = new Client();
            client.setCreatedAt(new Date());
            client.setCreatedById(user.getId());
        }
        client = clientRepository.save(client);
        String before = gson.toJson(client);

        client.setType(ApplicantType.Individual);

        Date passportDateOfIssue = DateParser.TryParse(individualDto.getPassportDateOfIssue(), Common.uzbekistanDateFormat);
        Date passportDateOfExpiry = DateParser.TryParse(individualDto.getPassportDateOfExpiry(), Common.uzbekistanDateFormat);

        client.setPinfl(individualDto.getIndividualPinfl());
        client.setTin(individualDto.getIndividualTin());
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

        clientAuditService.create(client.getId(),before,gson.toJson(client),message,user.getId(),user.getUserAdditionalId());
        return client;
    }

    //YaTT
    @Override
    public Client saveIndividualEntrepreneur(IndividualEntrepreneurDto individualEntrepreneurDto, User user, String message) {

        Client client = clientRepository.findTop1ByPinflAndDeletedFalseOrderByIdDesc(individualEntrepreneurDto.getIndividualEntrepreneurPinfl());
        if(client==null){
            client = new Client();
            client.setCreatedAt(new Date());
            client.setCreatedById(user.getId());
        }
        client = clientRepository.save(client);
        String before = gson.toJson(client);

        client.setType(ApplicantType.IndividualEnterprise);

        Date passportDateOfIssue = DateParser.TryParse(individualEntrepreneurDto.getEntrepreneurPassportDateOfIssue(), Common.uzbekistanDateFormat);
        Date passportDateOfExpiry = DateParser.TryParse(individualEntrepreneurDto.getEntrepreneurPassportDateOfExpiry(), Common.uzbekistanDateFormat);

        client.setOpfId(individualEntrepreneurDto.getIndividualEntrepreneurOpfId());
        client.setPinfl(individualEntrepreneurDto.getIndividualEntrepreneurPinfl());
        client.setTin(individualEntrepreneurDto.getIndividualEntrepreneurTin());
        client.setName(individualEntrepreneurDto.getIndividualEntrepreneurName());

        client.setPassportSerial(individualEntrepreneurDto.getEntrepreneurPassportSerial());
        client.setPassportNumber(individualEntrepreneurDto.getEntrepreneurPassportNumber());
        client.setPassportDateOfIssue(passportDateOfIssue);
        client.setPassportDateOfExpiry(passportDateOfExpiry);
        client.setPassportIssuedBy(individualEntrepreneurDto.getEntrepreneurPassportIssuedBy());

        client.setRegionId(individualEntrepreneurDto.getEntrepreneurRegionId());
        client.setSubRegionId(individualEntrepreneurDto.getEntrepreneurSubRegionId());
        client.setAddress(individualEntrepreneurDto.getEntrepreneurAddress());

        client.setPhone(individualEntrepreneurDto.getEntrepreneurPhone());
        client.setMobilePhone(individualEntrepreneurDto.getEntrepreneurMobilePhone());
        client.setEmail(individualEntrepreneurDto.getEntrepreneurEmail());

        client.setUpdateAt(new Date());
        client.setUpdateById(user.getId());
        client = clientRepository.save(client);

        clientAuditService.create(client.getId(),before,gson.toJson(client),message,user.getId(),user.getUserAdditionalId());
        return client;
    }

    //Xorijiy jismoniy shaxs
    @Override
    public Client saveForeignIndividual(ForeignIndividualDto foreignIndividualDto, User user, String message) {

        Client client = clientRepository.findTop1ByPassportNumberAndDeletedFalseOrderByIdDesc(foreignIndividualDto.getForeignPassportSerialNumber());
        if(client==null){
            client = new Client();
            client.setCreatedAt(new Date());
            client.setCreatedById(user.getId());
        }
        client = clientRepository.save(client);
        String before = gson.toJson(client);

        client.setType(ApplicantType.ForeignIndividual);


        client.setName(foreignIndividualDto.getForeignIndividualName());

        client.setPassportNumber(foreignIndividualDto.getForeignPassportSerialNumber());
        client.setCitizenshipId(foreignIndividualDto.getForeignCitizenshipId());

        client.setCountryId(foreignIndividualDto.getForeignCountryId());
        client.setAddress(foreignIndividualDto.getForeignAddress());

        client.setPhone(foreignIndividualDto.getForeignPhone());
        client.setMobilePhone(foreignIndividualDto.getForeignMobilePhone());
        client.setEmail(foreignIndividualDto.getForeignEmail());

        client.setUpdateAt(new Date());
        client.setUpdateById(user.getId());
        client = clientRepository.save(client);

        clientAuditService.create(client.getId(),before,gson.toJson(client),message,user.getId(),user.getUserAdditionalId());
        return client;
    }

    @Override
    public Client saveForEdit(Client client) {
        return clientRepository.save(client);
    }


    public Client updateGnk(Client client){
        client.setUpdateAt(new Date());
        return clientRepository.save(client);
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
                    predicates.add(criteriaBuilder.equal(root.get("type"), type.ordinal()));
                }

                if(tin!=null){
                    predicates.add(criteriaBuilder.equal(root.get("tin"), tin));
                }

                if(name!=null){
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%"+name.toLowerCase()+"%"));
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

    public void clientView(Integer applicantId, Model model){
        Client applicant = getById(applicantId);
        System.out.println("clientID" + applicantId);
        System.out.println(applicant.getOpfId() != null ?applicant.getOpfId():"000000000000");
        switch (applicant.getType()){
            case Individual:
                model.addAttribute("individual", new IndividualDto(applicant)); break;
            case LegalEntity:
                model.addAttribute("legalEntity", new LegalEntityDto(applicant)) ;break;
            case ForeignIndividual:
                model.addAttribute("foreignIndividual", new ForeignIndividualDto(applicant)); break;
            case IndividualEnterprise:
                model.addAttribute("individualEntrepreneur", new IndividualEntrepreneurDto(applicant)); break;
        }
        model.addAttribute("applicant", applicant);
        String locale = LocaleContextHolder.getLocale().toLanguageTag();
        String opfName="";
        if (locale.equals("ru")){
            opfName=applicant.getOpfId()!=null?helperService.getOpfShortName(applicant.getOpfId(),locale):"";
            opfName += "  \"" + applicant.getName() + "\"";
        }else{
            opfName ="\"" + applicant.getName() + "\" ";
            opfName += applicant.getOpfId()!=null?helperService.getOpfShortName(applicant.getOpfId(),locale):"";
        }
        model.addAttribute("opfId", applicant.getOpfId());
        model.addAttribute("opfName", opfName);
    }

}
