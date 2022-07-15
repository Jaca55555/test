package uz.maroqand.ecology.core.service.expertise.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.*;
import uz.maroqand.ecology.core.constant.sys.SmsSendStatus;
import uz.maroqand.ecology.core.dto.expertise.FilterDto;
import uz.maroqand.ecology.core.dto.sms.AuthTokenInfo;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.expertise.*;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.sys.SmsSend;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.integration.sms.SmsSendOauth2Service;
import uz.maroqand.ecology.core.repository.expertise.RegApplicationRepository;
import uz.maroqand.ecology.core.service.client.ClientService;
import uz.maroqand.ecology.core.service.expertise.FactureService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationLogService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.expertise.RequirementService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;
import uz.maroqand.ecology.core.service.sys.SmsSendService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class RegApplicationServiceImpl implements RegApplicationService {

    private final RegApplicationRepository regApplicationRepository;
    private final SmsSendService smsSendService;
    private final SmsSendOauth2Service smsSendOauth2Service;
    private final UserService userService;
    private final RegApplicationLogService regApplicationLogService;
    private final ClientService clientService;
    private final OrganizationService organizationService;
    private final RequirementService requirementService;
    private final FactureService factureService;

    @Autowired
    public RegApplicationServiceImpl(RegApplicationRepository regApplicationRepository, SmsSendService smsSendService, SmsSendOauth2Service smsSendOauth2Service, UserService userService, RegApplicationLogService regApplicationLogService, ClientService clientService, OrganizationService organizationService, RequirementService requirementService, FactureService factureService) {
        this.regApplicationRepository = regApplicationRepository;
        this.smsSendService = smsSendService;
        this.smsSendOauth2Service = smsSendOauth2Service;
        this.userService = userService;
        this.regApplicationLogService = regApplicationLogService;
        this.clientService = clientService;
        this.organizationService = organizationService;
        this.requirementService = requirementService;
        this.factureService = factureService;
    }

    @Override
    public RegApplication create(User user,RegApplicationInputType inputType,RegApplicationCategoryType categoryType){
        RegApplication regApplication = new RegApplication();
        regApplication.setInputType(inputType);
        regApplication.setRegApplicationCategoryType(categoryType);
        regApplication.setCreatedAt(new Date());
        regApplication.setCreatedById(user.getId());
        regApplication.setStatus(RegApplicationStatus.Initial);
        regApplication.setCategoryFourStep(RegApplicationCategoryFourStep.APPLICANT);

        regApplicationRepository.save(regApplication);
        return regApplication;
    }

    @Override
    public RegApplication getByOfferId(Integer offerId) {
        return regApplicationRepository.findByOfferIdAndDeletedFalse(offerId);
    }

    @Override
    public List<RegApplication> getByClientId(Integer id) {
        return regApplicationRepository.findByApplicantId(id);
    }

    @Override
    public List<RegApplication> getByClientIdDeletedFalse(Integer id) {
        return regApplicationRepository.findByApplicantIdAndDeletedFalse(id);
    }

    @Override
    public List<RegApplication> findByDelivered() {
        return regApplicationRepository.findAllByDeliveryStatusAndDeletedFalse((short) 0);
    }

    @Override
    public List<RegApplication> getByInvoiceId(Integer invoiceId) {
        return regApplicationRepository.findByInvoiceId(invoiceId);
    }

    @Override
    public RegApplication getByOneInvoiceId(Integer invoiceId) {
        return regApplicationRepository.findByInvoiceIdAndDeletedFalse(invoiceId);
    }

    @Override
    public RegApplication getTopByOneInvoiceId(Integer invoiceId) {
        return regApplicationRepository.findTop1ByInvoiceIdAndDeletedFalse(invoiceId);
    }

    @Override
    public RegApplication updateBoiler(RegApplication regApplication, Integer userId) {
        regApplication.setUpdateAt(new Date());
        regApplication.setUpdateById(userId);
        return  regApplicationRepository.save(regApplication);
    }

    @Override
    public List<RegApplication> getAllByPerfomerIdNotNullDeletedFalse() {
        return regApplicationRepository.findAllByPerformerIdNotNullAndDeletedFalseOrderByIdDesc();
    }

    //1 --> yuborildi
    //2 --> arizachi topilmadi
    //3 --> bu raqamga jo'natib bo'lmaydi
    @Override
    public Integer sendSMSCode(String mobilePhone,Integer regApplicationId) {
        RegApplication regApplication = getById(regApplicationId);
        if (regApplication==null){
            return 2;
        }

        String phoneNumber = smsSendService.getPhoneNumber(mobilePhone);
        if (phoneNumber.isEmpty()){
            return 3;
        }

        String code = createSMSCode();
        System.out.println("code=" + code);
        SmsSend smsSend = new SmsSend();
        smsSend.setMessage(code);
        smsSend.setFullName(regApplication.getName());
        smsSend.setStatus(SmsSendStatus.SCHEDLD);
        smsSend.setDeleted(Boolean.FALSE);
        smsSend.setPhone(phoneNumber);
        smsSend.setRegApplicationId(regApplicationId);
        smsSendService.save(smsSend);
        System.out.println("sms ketdi");
        AuthTokenInfo authTokenInfo = smsSendOauth2Service.getAccessTokenCheck();
        smsSendOauth2Service.createContact(authTokenInfo,smsSend);
        SmsSend smsSendSent = smsSendOauth2Service.createSendTask(authTokenInfo,smsSend);
        smsSendService.update(smsSendSent);
        return 1;
    }

    public void update(RegApplication regApplication){
        regApplication.setUpdateAt(new Date());
        regApplicationRepository.save(regApplication);
    }

    @Override
    public void cancelModification() {
        List<RegApplicationStatus> statusList = new LinkedList<>();
        statusList.add(RegApplicationStatus.ModificationCanceled);
        statusList.add(RegApplicationStatus.Modification);
        statusList.add(RegApplicationStatus.Process);
        List<RegApplication> regApplicationIds = regApplicationRepository.findAllByStatusInAndDeletedFalseOrderByIdDesc(statusList);
        for(RegApplication regApplication:regApplicationIds){
            List<RegApplicationLog> modificationRegApplicationLogList = regApplicationLogService.findByStatusAndType(regApplication.getId(),LogStatus.Modification,LogType.Performer);
            List<RegApplicationLog> agreementRegApplicationLogList = regApplicationLogService.findByStatusAndType(regApplication.getId(),LogStatus.Approved,LogType.Agreement);
            List<RegApplicationLog> agreementCompleteRegApplicationLogList = regApplicationLogService.findByStatusAndType(regApplication.getId(),LogStatus.Approved,LogType.AgreementComplete);
            List<RegApplicationLog> conclusionCompleteRegApplicationLogList = regApplicationLogService.findByStatusAndType(regApplication.getId(),LogStatus.Approved,LogType.ConclusionComplete);

            if(modificationRegApplicationLogList.size()>=2 && agreementCompleteRegApplicationLogList.size()%2==0 && agreementRegApplicationLogList.size()%2==0 && conclusionCompleteRegApplicationLogList.size()%2==0){
                regApplication.setStatus(RegApplicationStatus.ModificationCanceled);
                regApplication.setUpdateAt(new Date());
                regApplicationRepository.save(regApplication);
    }
        }
    }

    @Override
    public void closeModificationTimer() {
        List<RegApplicationStatus> statusList = new LinkedList<>();
        statusList.add(RegApplicationStatus.ModificationCanceled);
        statusList.add(RegApplicationStatus.Modification);
        statusList.add(RegApplicationStatus.Process);
        List<RegApplication> regApplicationIds = regApplicationRepository.findAllByStatusInAndDeletedFalseOrderByIdDesc(statusList);
        for(RegApplication regApplication:regApplicationIds){
            RegApplicationLog regApplicationLog = regApplicationLogService.findTop1ByStatusAndType(regApplication.getId(),LogStatus.Modification,LogType.Performer);
            if(regApplicationLog!=null){
                Date createdDate = regApplicationLog.getCreatedAt();
                Calendar c = Calendar.getInstance();
                Date date = new Date();
                c.setTime(date);
                c.add(Calendar.DATE,-61);    // shu kunning o'zi ham qo'shildi
                Date expireDate = c.getTime();

                if (createdDate.before(expireDate)){
                    if(regApplication.getStatus()==RegApplicationStatus.Modification){
                        regApplication.setStatus(RegApplicationStatus.ModificationCanceled);
                        regApplication.setUpdateAt(new Date());
                        regApplicationRepository.save(regApplication);
                    }
                }
            }

        }




    }

    public RegApplication getById(Integer id) {
        if(id==null) return null;
        return regApplicationRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public RegApplication getByContractNumber(String contractNumber) {
        return regApplicationRepository.findByContractNumber(contractNumber);
    }

    @Override
    public RegApplication getByIdAndUserTin(Integer id, User user) {

        System.out.println("getByIdAndUserTin");
        if (user==null){
            System.out.println("if  user==null");
            return null;
        }

        if (user.getTin()!=null){

            List<Client> clientList = clientService.getByListTin(user.getTin());
            System.out.println("if  user.getTin()!=null  " + clientList.size());
            RegApplication regApplication = getRegApplication(id, clientList);
            if (regApplication != null) return regApplication;
        }

        if(user.getPinfl() != null){
            List<Client> clientList = clientService.getByListPinfl(user.getPinfl());
            System.out.println("if  user.getPinfl()!=null  " + clientList.size());
            RegApplication regApplication = getRegApplication(id, clientList);
            if (regApplication != null) return regApplication;
        }

        if (user.getLeTin()!=null){

            List<Client> clientList = clientService.getByListTin(user.getLeTin());
            System.out.println("if  user.getLeTin()!=null  " + clientList.size());
            RegApplication regApplication = getRegApplication(id, clientList);
            if (regApplication != null) return regApplication;
        }
        System.out.println("else");

        return null;
    }

    private RegApplication getRegApplication(Integer id, List<Client> clientList) {
        for (Client client: clientList) {
            List<RegApplication> regApplications = getByClientIdDeletedFalse(client.getId());
            for (RegApplication regApplication :regApplications){
                if (regApplication.getId().equals(id)){
                    System.out.println("if  regApplication.getId().equals(id)");
                    return regApplication;
                }
            }
        }
        return null;
    }

//    @Override
//    public List<RegApplicationDTO> listByTin(Integer tin) {
//        Client client=clientService.getByTin(tin);
//        List<RegApplication> regApplicationList=getByClientIdDeletedFalse(client.getId());
////        System.out.println("regApplicationList="+regApplicationList);
//        return RegApplicationDTO.listFromEntity(regApplicationList);
//    }

    @Override
    public RegApplication sendRegApplicationAfterPayment(RegApplication regApplication,User user, Invoice invoice, String locale) {
        if (regApplication!=null && regApplication.getForwardingLogId() == null){
                regApplication.setLogIndex(1);
                RegApplicationLog regApplicationLog = regApplicationLogService.create(regApplication,LogType.Forwarding,"",user);
                regApplication.setForwardingLogId(regApplicationLog.getId());
                regApplication.setStatus(RegApplicationStatus.Process);
                regApplication.setRegistrationDate(new Date());
                regApplication.setDeadlineDate(regApplicationLogService.getDeadlineDate(regApplication.getDeadline(), new Date()));

                if (regApplication.getInputType()!=null && regApplication.getInputType().equals(RegApplicationInputType.ecoService)){
                    Client client = clientService.getById(regApplication.getApplicantId());
                    Organization organization = organizationService.getById(regApplication.getReviewId());
                    Requirement requirement = requirementService.getById(regApplication.getRequirementId());
                    Facture facture = factureService.create(regApplication, client, organization, requirement, invoice, locale);
                    regApplication.setFactureId(facture.getId());
                }

                update(regApplication);
        }
        return regApplication;
    }

    @Override
    public RegApplication cancelApplicationByInvoiceId(Integer invoiceId) {
        RegApplication regApplication = getByOneInvoiceId(invoiceId);
        if (regApplication==null) return null;
        regApplication.setStatus(RegApplicationStatus.Canceled);
        update(regApplication);
        System.out.println("==cancelApplicationByInvoiceId==");
        return regApplication;
    }

    @Override
    public List<RegApplication> getListByPerformerId(Integer performerId) {
        return regApplicationRepository.findAllByPerformerIdAndDeletedFalseOrderByIdDesc(performerId);
    }

    public RegApplication getById(Integer id, Integer createdBy) {
        if(id==null) return null;
        return regApplicationRepository.findByIdAndCreatedByIdAndDeletedFalse(id, createdBy);
    }

    @Override
    public Boolean beforeOrEqualsTrue(RegApplication regApplication) {
        if ((regApplication.getStatus().equals(RegApplicationStatus.Approved) || regApplication.getStatus().equals(RegApplicationStatus.NotConfirmed))
                && regApplication.getDeadlineDate()!=null  && regApplication.getAgreementCompleteLogId()!=null){
            RegApplicationLog conclusionCompleteLog =regApplicationLogService.getById(regApplication.getAgreementCompleteLogId());
            if (conclusionCompleteLog!=null && conclusionCompleteLog.getStatus().equals(LogStatus.Approved) && conclusionCompleteLog.getUpdateAt()!=null){
                Date deadlineDate = conclusionCompleteLog.getUpdateAt();
                Date regDeadline = regApplication.getDeadlineDate();
                deadlineDate.setHours(0);
                deadlineDate.setMinutes(0);
                deadlineDate.setSeconds(0);
                regDeadline.setHours(0);
                regDeadline.setMinutes(0);
                regDeadline.setSeconds(0);
                if (regDeadline.equals(deadlineDate) || regDeadline.after(deadlineDate)){
                    return Boolean.TRUE;
                }else {
                    return Boolean.FALSE;
                }
            }
        }

        return null;
    }

    @Override
    public Page<RegApplication> findFiltered(
            FilterDto filterDto,
            Integer reviewId,
            LogType logType,
            Integer performerId,
            Integer userId,
            RegApplicationInputType regApplicationInputType,
            Pageable pageable
    ) {
        return regApplicationRepository.findAll(getFilteringSpecification(filterDto, reviewId, logType, performerId, userId,regApplicationInputType),pageable);
    }

    @Override
    public Integer countByCategoryAndStatusAndRegionId(Category category,Date dateBegin,Date dateEnd, RegApplicationStatus status, Integer regionId,Set<Integer> organizationIds) {
        if(status!=null){
            if (category!=null){
                return regApplicationRepository.countByCategoryAndStatusAndRegionId(category,dateBegin,dateEnd,status,regionId,organizationIds);
            }else{
                return regApplicationRepository.countByStatusAndRegionId(dateBegin,dateEnd,status,regionId,organizationIds);
            }
        }else {
            if (category!=null){
                return regApplicationRepository.countByCategoryAndRegionId(category,dateBegin,dateEnd,regionId,organizationIds);
            }else{
                return regApplicationRepository.countByRegionId(dateBegin,dateEnd,regionId,organizationIds);
            }
        }
    }

    @Override
    public Integer countByCategoryAndStatusAndSubRegionId(Category category,Date dateBegin,Date dateEnd, RegApplicationStatus status, Integer subRegionId,Set<Integer> organizationIds) {
        if(status!=null){
            if(category!=null){
                return regApplicationRepository.countByCategoryAndStatusAndSubRegionId(category,dateBegin,dateEnd,status,subRegionId,organizationIds);
            }else {
                return regApplicationRepository.countByStatusAndSubRegionId(dateBegin,dateEnd,status,subRegionId,organizationIds);
            }
        }
        else {
            if (category!=null){
                return regApplicationRepository.countByCategoryAndSubRegionId(category,dateBegin,dateEnd,subRegionId,organizationIds);
            }else {
                return regApplicationRepository.countBySubRegionId(dateBegin,dateEnd,subRegionId,organizationIds);
            }
        }
    }


    @Override
    public long findFilteredNumber(LogType logType, Integer integer) {
        FilterDto filterDto = new FilterDto();
        filterDto.setStatusing(1);
        Pageable pageable =new PageRequest(0,10);

        long returnElement = regApplicationRepository.findAll(getFilteringSpecification(filterDto, integer, logType, null, null,null),pageable).getTotalElements();
        return returnElement;
    }

    private static Specification<RegApplication> getFilteringSpecification(
            final FilterDto filterDto,
            final Integer reviewId,
            final LogType logType,
            final Integer performerId,
            final Integer userId,
            final RegApplicationInputType regApplicationInputType
    ) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            //Ko'rib chiquvchi organization
            if(reviewId!=null){
                predicates.add(criteriaBuilder.equal(root.get("reviewId"),reviewId));
            }

            System.out.println("logType="+logType);
            if(logType!=null){
                switch (logType){
                    case Confirm:
                        predicates.add(criteriaBuilder.isNotNull(root.get("confirmLogId")));break;
                    case Forwarding:
                        predicates.add(criteriaBuilder.isNotNull(root.get("forwardingLogId")));break;
                    case Performer:
                        predicates.add(criteriaBuilder.isNotNull(root.get("performerLogId")));break;
                    /*case Agreement:
                        predicates.add(criteriaBuilder.isNotNull(root.get("agreementLogId")));break;*/
                    case AgreementComplete:
                        predicates.add(criteriaBuilder.isNotNull(root.get("agreementCompleteLogId")));break;
                }
            }

            if (filterDto!=null) {
                if (filterDto.getRegApplicationStatus()!=null){
//                        System.out.println(filterDto.getRegApplicationStatus().getName());
                    predicates.add(criteriaBuilder.equal(root.get("status"),filterDto.getRegApplicationStatus()));
                }
                if(filterDto.getRegApplicationCategoryType()!=null){
                    predicates.add(criteriaBuilder.equal(root.get("regApplicationCategoryType"),filterDto.getRegApplicationCategoryType()));
                }
                if (filterDto.getStatusForReg()!=null){
                    predicates.add(criteriaBuilder.in(root.get("status")).value(filterDto.getStatusForReg()));
                }


//                    if(filterDto.getContractNumber()!=null && !filterDto.getContractNumber().isEmpty()){
//                        predicates.add(criteriaBuilder.like(root.get("contractNumber"),"%" + StringUtils.trimToNull(filterDto.getContractNumber()) + "%"));
//                    }
                if (filterDto.getTin() != null) {
                    predicates.add(criteriaBuilder.equal(root.join("applicant").get("tin"), filterDto.getTin()));
                }
                if (StringUtils.trimToNull(filterDto.getName()) != null) {
                    predicates.add(criteriaBuilder.like(root.join("applicant").<String>get("name"), "%" + StringUtils.trimToNull(filterDto.getName()) + "%"));
                }
                if (filterDto.getApplicationId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), filterDto.getApplicationId()));
                }
                if(filterDto.getOrganizationId() != null){
                    predicates.add(criteriaBuilder.equal(root.get("reviewId"),filterDto.getOrganizationId()));
                }
                if (filterDto.getRegionId() != null) {
                    predicates.add(criteriaBuilder.equal(root.join("applicant").get("regionId"), filterDto.getRegionId()));
                }
                if(filterDto.getCategory()!=null){
                    predicates.add(criteriaBuilder.equal(root.get("category"),filterDto.getCategory()));
                }
                if (filterDto.getSubRegionId() != null) {
                    predicates.add(criteriaBuilder.equal(root.join("applicant").get("subRegionId"), filterDto.getSubRegionId()));
                }
                if (filterDto.getStatus() != null) {
                    predicates.add(criteriaBuilder.equal(root.join("forwardingLog").get("status"), LogStatus.getLogStatus(filterDto.getStatus())));
                }


                    if (filterDto.getTin() != null) {
                        predicates.add(criteriaBuilder.equal(root.join("applicant").get("tin"), filterDto.getTin()));
                    }
                    if (StringUtils.trimToNull(filterDto.getName()) != null) {
                        predicates.add(criteriaBuilder.like(root.<String>get("name"), "%" + StringUtils.trimToNull(filterDto.getName()) + "%"));
                    }
                    if (filterDto.getApplicationId() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("id"), filterDto.getApplicationId()));
                }else {
                    if (logType != null) {
                        List<Integer> init = new ArrayList<>();
                        init.add(0);
                        init.add(1);
                        init.add(2);
                        switch (logType) {
                            case Forwarding:
                                predicates.add(criteriaBuilder.in(root.get("forwardingLogId")).value(init));
                                break;
                            case Performer:
                                predicates.add(criteriaBuilder.in(root.get("performerLogId")).value(init));
                                break;
                        }
                    }
                    if(filterDto.getOrganizationId() != null){
                        predicates.add(criteriaBuilder.equal(root.get("reviewId"),filterDto.getOrganizationId()));
                   }
                    if (filterDto.getRegionId() != null) {
                        predicates.add(criteriaBuilder.equal(root.join("applicant").get("regionId"), filterDto.getRegionId()));
                    }
                    if(filterDto.getCategory()!=null){
                        predicates.add(criteriaBuilder.equal(root.get("category"),filterDto.getCategory()));
                    }
                    if (filterDto.getSubRegionId() != null) {
                        predicates.add(criteriaBuilder.equal(root.join("applicant").get("subRegionId"), filterDto.getSubRegionId()));
                    }

                Date regDateBegin = DateParser.TryParse(filterDto.getRegDateBegin(), Common.uzbekistanDateFormat);
                Date regDateEnd = DateParser.TryParse(filterDto.getRegDateEnd(), Common.uzbekistanDateFormat);

                if (regDateBegin != null && regDateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt").as(Date.class), regDateBegin));
                }
                if (regDateEnd != null && regDateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt").as(Date.class), regDateEnd));
                }
                if (regDateBegin != null && regDateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.get("createdAt").as(Date.class), regDateBegin, regDateEnd));
                }
                Date deadlineDate = DateParser.TryParse(filterDto.getDeadlineDate(), Common.uzbekistanDateFormat);

                if (deadlineDate != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("deadlineDate").as(Date.class),deadlineDate));
                }

                Date dateBegin = DateParser.TryParse(filterDto.getDateBegin(), Common.uzbekistanDateFormat);
                Date dateEnd = DateParser.TryParse(filterDto.getDateEnd(), Common.uzbekistanDateFormat);
//                    System.out.println("dateBegin"+dateBegin);
//                    System.out.println("dateEnd"+dateEnd);
                if (dateBegin != null && dateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("confirmLogAt").as(Date.class), dateBegin));
                }
                if (dateEnd != null && dateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("confirmLogAt").as(Date.class), dateEnd));
                }
                if (dateBegin != null && dateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.get("confirmLogAt").as(Date.class), dateBegin, dateEnd));
                }

                Date deadlineDateBegin = DateParser.TryParse(filterDto.getDeadlineDateBegin(), Common.uzbekistanDateFormat);
                Date deadlineDateEnd = DateParser.TryParse(filterDto.getDeadlineDateEnd(), Common.uzbekistanDateFormat);
                if (deadlineDateBegin != null && deadlineDateEnd == null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("deadlineDate").as(Date.class), deadlineDateBegin));
                }
                if (deadlineDateEnd != null && deadlineDateBegin == null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("deadlineDate").as(Date.class), deadlineDateEnd));
                }
                if (deadlineDateBegin != null && deadlineDateEnd != null) {
                    predicates.add(criteriaBuilder.between(root.get("deadlineDate").as(Date.class), deadlineDateBegin, deadlineDateEnd));
                }

                if (filterDto.getActivityId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("activityId"), filterDto.getActivityId()));
                }

                if (filterDto.getConclusionOnline() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("conclusionOnline"), filterDto.getConclusionOnline()));
                }

                if (filterDto.getObjectId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("objectId"), filterDto.getObjectId()));
                }
            }
            if(performerId!=null){
                predicates.add(criteriaBuilder.equal(root.get("performerId"), performerId));
            }

            if(userId!=null){
                System.out.println("userId");
                Predicate byUserId, byLeTin, byTin;
                byUserId = criteriaBuilder.equal(root.get("createdById"), userId);
                if (filterDto!=null && (filterDto.getByLeTin()!=null || filterDto.getByTin()!=null)
                        && regApplicationInputType!=null && regApplicationInputType.equals(RegApplicationInputType.ecoService)
                ){
                    if (filterDto.getByLeTin()!=null && filterDto.getByTin()!=null){
                        byLeTin = criteriaBuilder.equal(root.join("applicant").get("tin"), filterDto.getByLeTin());
                        byTin = criteriaBuilder.equal(root.join("applicant").get("tin"), filterDto.getByTin());
                        predicates.add(criteriaBuilder.or(byLeTin, byTin,byUserId));
                    }else{
                        if (filterDto.getByTin()!=null && filterDto.getByLeTin()==null){
                            byTin = criteriaBuilder.equal(root.join("applicant").get("tin"), filterDto.getByTin());
                            predicates.add(criteriaBuilder.or(byTin, byUserId));
                        }else{
                            if (filterDto.getByTin()==null && filterDto.getByLeTin()!=null) {
                                byLeTin = criteriaBuilder.equal(root.join("applicant").get("tin"), filterDto.getByLeTin());
                                predicates.add(criteriaBuilder.or(byLeTin,byUserId));
                            }
                        }
                    }
                }else{
                    predicates.add(byUserId);
                }
            }

            if(regApplicationInputType!=null){
                predicates.add(criteriaBuilder.equal(root.get("inputType"), regApplicationInputType.ordinal()));
            }

            Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
            predicates.add( notDeleted );
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private String createSMSCode() {
        return String.valueOf(100000L + (long) (new Random().nextDouble() * 899999L));
    }
}
