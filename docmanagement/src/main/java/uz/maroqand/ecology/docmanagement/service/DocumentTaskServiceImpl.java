package uz.maroqand.ecology.docmanagement.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.docmanagement.constant.DocumentStatus;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.constant.TaskStatus;
import uz.maroqand.ecology.docmanagement.constant.TaskSubType;
import uz.maroqand.ecology.docmanagement.dto.IncomingRegFilter;
import uz.maroqand.ecology.docmanagement.dto.ReferenceRegFilterDTO;
import uz.maroqand.ecology.docmanagement.dto.ResolutionDTO;
import uz.maroqand.ecology.docmanagement.dto.StaticInnerInTaskSubDto;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 11.02.2020
 */

@Service
public class DocumentTaskServiceImpl implements DocumentTaskService{

    private final DocumentTaskRepository taskRepository;
    private final DocumentService documentService;
    private final HelperService helperService;
    private final DocumentTaskSubService taskSubService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public DocumentTaskServiceImpl(DocumentTaskRepository taskRepository, DocumentService documentService, HelperService helperService, DocumentTaskSubService taskSubService, UserService userService, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.documentService = documentService;
        this.helperService = helperService;
        this.taskSubService = taskSubService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    public void createNotificationForAddComment(Document document, DocumentTaskSub documentTaskSub) {
        List<DocumentTaskSub> documentTaskSubList = taskSubService.getListByDocIdAndTaskId(document.getId(),documentTaskSub.getTaskId());
        for (DocumentTaskSub taskSub: documentTaskSubList) {
            if (!taskSub.getReceiverId().equals(documentTaskSub.getReceiverId())){
                notificationService.create(
                        taskSub.getReceiverId(),
                        NotificationType.Document,
                        "doc_notification.add_comment",
                        document.getRegistrationNumber(),
                        "doc_notification_message.add_comment",
                        taskSubService.getUrl(document,taskSub.getId()),
                        documentTaskSub.getReceiverId()
                );
            }
        }
    }

    @Override
    public DocumentTask getById(Integer id) {
        Optional<DocumentTask> task = taskRepository.findById(id);
        return task.orElse(null);
    }

    @Override
    public DocumentTask getByIdAndDocumentId(Integer id, Integer docId) {
        return taskRepository.findByIdAndDocumentIdAndDeletedFalse(id,docId);
    }

    @Override
    public List<DocumentTask> getByDocumetId(Integer docId) {
        return taskRepository.findByDocumentIdOrderByIdAsc(docId);
    }

    @Override
    public List<DocumentTask> getByStatusNotInactive() {
        return null;
    }

    @Override
    public DocumentTask create(DocumentTask task) {
        return taskRepository.save(task);
    }

    @Override
    public Integer countNewForReference() {
        Set<Integer> status = new HashSet<>();
        status.add(TaskStatus.Initial.getId());
        Integer Initial=taskRepository.countByReceiverIdAndStatusForReference(status);
        status.clear();
        status.add(TaskStatus.New.getId());
        Integer New=taskRepository.countByReceiverIdAndStatusForReference(status);
        return New+Initial;
    }

    public Integer countNew() {
        Set<Integer> status = new HashSet<>();
        status.add(TaskStatus.Initial.getId());
        status.add(TaskStatus.New.getId());
        return taskRepository.countByStatusInAndDeletedFalse(status);
    }

    public Integer countInProcess() {
        Set<Integer> status = new LinkedHashSet<>();
        status.add(TaskStatus.InProgress.getId());
        return taskRepository.countByStatusInAndDeletedFalse(status);
    }
    public Integer countInProcessForReference() {
        Set<Integer> status = new LinkedHashSet<>();
        status.add(TaskStatus.InProgress.getId());
        return taskRepository.countByReceiverIdAndStatusForReference(status);
    }

    public Integer countNearDate() {
        Calendar calendar = Calendar.getInstance();
        Date begin = calendar.getTime();
        calendar.add(Calendar.DAY_OF_WEEK, 1);
        Date end = calendar.getTime();
        return taskRepository.countByDueDateBetweenAndStatusNotAndDeletedFalse(begin, end, TaskStatus.Complete.getId());
    }

    public Integer countExpired() {
        return taskRepository.countByDueDateBeforeAndStatusNotAndDeletedFalse(new Date(), TaskStatus.Complete.getId());
    }

    public Integer countExecuted() {
        Set<Integer> status = new LinkedHashSet<>();
        status.add(TaskStatus.Complete.getId());
        return taskRepository.countByStatusInAndDeletedFalse(status);
    }
    public Integer countExecutedForReference() {
        Set<Integer> status = new LinkedHashSet<>();
        status.add(TaskStatus.Complete.getId());
        return taskRepository.countByStatusInAndDeletedFalse(status);
    }

    public Integer countTotal() {

        return taskRepository.countByDeletedFalse();
    }

    @Override
    public Integer getCountTaskStatus(Set<Integer> statusSet) {
        return taskRepository.countByStatusInAndDeletedFalse(statusSet);
    }

    @Override
    public DocumentTask createNewTask(Document doc, Integer status, String context, Date dueDate, Integer chiefId, Integer createdById) {
        DocumentTask documentTask = new DocumentTask();
        documentTask.setDocumentId(doc.getId());
        documentTask.setStatus(status);
        documentTask.setContent(context.trim());
        documentTask.setDueDate(dueDate);
        documentTask.setChiefId(chiefId);
        documentTask.setCreatedAt(new Date());
        documentTask.setCreatedById(createdById);
        documentTask.setDeleted(Boolean.FALSE);

        if (doc.getStatus().equals(DocumentStatus.New)){
            doc.setStatus(DocumentStatus.InProgress);
            documentService.update(doc);
        }
        return taskRepository.save(documentTask);
    }

    @Override
    public DocumentTask update(DocumentTask task) {
        return taskRepository.save(task);
    }

    public DocumentTask getTaskByUser(Integer docId, Integer userId) {
        return taskRepository.findByDocumentIdAndChiefId(docId, userId);
    }

    @Override
    public String getTreeByDocumentId(List<DocumentTask> documentTasks) {
        String tree = "";
        String locale = LocaleContextHolder.getLocale().getLanguage();
        for (DocumentTask documentTask: documentTasks){
            String getReceiverName =  helperService.getUserFullNameById(documentTask.getChiefId()) +" ("+ helperService.getPositionName(documentTask.getChief().getPositionId(),locale)+")";
            tree+= "{ text :\"" + getReceiverName + " \",tags:" + documentTask.getId();
            List<DocumentTaskSub> taskSubs = taskSubService.getListByDocIdAndTaskId(documentTask.getDocumentId(),documentTask.getId());
            if (taskSubs.size()>0){
                tree+=" , nodes:" + taskSubService.buildTree(1,taskSubs,new HashMap<>(),locale) + "},";
            }else{
                tree+="},";
            }
        }
        return tree;
    }

    @Override
    public Page<DocumentTask> findFiltered(
            Integer organizationId,
            Integer documentTypeId,
            IncomingRegFilter incomingRegFilter,
            Date deadlineDateBegin,
            Date deadlineDateEnd,
            Set<Integer> status,
            Integer departmentId,
            Integer receiverId,
            Boolean specialControll,
            Pageable pageable
    ) {
        return taskRepository.findAll(getSpecification(organizationId, documentTypeId, incomingRegFilter, deadlineDateBegin, deadlineDateEnd, status, departmentId, receiverId,specialControll), pageable);
    }

    @Override
    public Page<DocumentTask> findFilteredReference(Integer organizationId, Integer documentTypeId, ReferenceRegFilterDTO referenceRegFilterDTO, Date deadlineDateBegin, Date deadlineDateEnd, Integer type, Set<Integer> status, Integer departmentId, Integer receiverId, Boolean specialControll, Pageable pageable) {
        return taskRepository.findAll(getSpecificationReference(organizationId, documentTypeId, referenceRegFilterDTO, deadlineDateBegin, deadlineDateEnd, type, status, departmentId, receiverId,specialControll), pageable);
    }

    //taskOrsubTask==true  task
    //taskOrsubTask==false  taskSub
    @Override
    public List<String> getDueColor(Date date, boolean taskOrTaskSub, Integer statusId,String locale) {
        List<String> result = new ArrayList<>();
        System.out.println(statusId);
        String colorText="";
        if (statusId==3){ colorText+=" color:yellow; " ;}else
        if (statusId==4){ colorText+=" color:yellow; " ;}else
        if (statusId==5){ colorText+=" color:grey; " ;}else
        if (statusId==6){ colorText+=" color:green; " ;}else
        if (statusId==7){ colorText+=" color:red; " ;}else
        if (statusId==8){ colorText+=" color:rgb(169,169,169); " ;}

        if (statusId == null||date==null){
            result.add(colorText);
            return result;
        }
        Date nowDate = new Date();

        date.setHours(23);
        date.setMinutes(59);
        Long intervalHours = (date.getTime()-nowDate.getTime())/3600000;
        System.out.println(intervalHours);

        if (statusId==1 || statusId==0){ colorText+=" color:blue;font-weight: bold; ";}else
        if (statusId==2){if(intervalHours>72) {colorText+=" color:blue;" ;}else
        if(intervalHours<72&&intervalHours>24) {colorText+=" color:orange; ";}
        else if(intervalHours<24) {colorText+="color:red; " ;}
        }

        result.add(colorText);

        return result;
    }

    @Override
    public String getDueTranslateNameOrColor(Integer id, boolean taskOrsubTask,String nameOrColor, String locale) {
        if (id==null){
            if (nameOrColor.equals("name")){
                return "";
            }else{
                return "danger";
            }
        }
        Date dueDate;
        Date nowDate = new Date();
        if (taskOrsubTask){
            DocumentTask task = getById(id);
            if (task==null || task.getDueDate()==null){
                if (nameOrColor.equals("name")){
                    return "";
                }else{
                    return "danger";
                }
            }
            dueDate=task.getDueDate();
        }else{
            DocumentTaskSub taskSub = taskSubService.getById(id);
            if (taskSub==null || taskSub.getDueDate()==null){
                if (nameOrColor.equals("name")){
                    return "";
                }else{
                    return "danger";
                }
            }
            dueDate=taskSub.getDueDate();
        }

        Long interval = (dueDate.getTime()-nowDate.getTime())/3600000;

        if (interval<=0){
            if (nameOrColor.equals("name")){
                return helperService.getTranslation("sys_due.passed",locale);
            }else{
                return "danger";
            }
        }else if (interval<=72){
            if (nameOrColor.equals("name")){
                return helperService.getTranslation("sys_due.left",locale);
            }else{
                return "warning";
            }
        }

        if (nameOrColor.equals("name")){
            return "";
        }else{
            return "danger";
        }
    }

    @Override
    public StaticInnerInTaskSubDto countAllInnerByReceiverId(Integer receiverId) {
        StaticInnerInTaskSubDto statisticInner = new StaticInnerInTaskSubDto();
        if (receiverId==null) return statisticInner;
        List<DocumentTask> documentTaskList = taskRepository.findByChiefIdAndDeletedFalseOrderByIdAsc(receiverId);
        for (DocumentTask documentTask: documentTaskList){
            if (documentTask.getDocument().getDocumentTypeId().equals(DocumentTypeEnum.InnerDocuments.getId())){//todo vaqtincha to'g'rilash kerak
                statisticInner.setAllCount(statisticInner.getAllCount()+1);                         //barcha ichki xatlar

                if (documentTask.getStatus()!=null){
                    if (documentTask.getStatus().equals(TaskStatus.New.getId())
                            || documentTask.getStatus().equals(TaskStatus.Initial.getId())){
                        statisticInner.setNewCount(statisticInner.getNewCount()+1);                     //yangi
                    }

                    if (documentTask.getStatus().equals(TaskStatus.InProgress.getId())){
                        statisticInner.setInProgressCount(statisticInner.getInProgressCount()+1);       //jarayondagilar
                    }

                    if (documentTask.getStatus().equals(TaskStatus.Checking.getId())){
                        statisticInner.setCheckingCount(statisticInner.getCheckingCount()+1);                     //ijro etilganlar, judayam zor
                    }

                }

                Date nowDate = new Date();
                nowDate.setHours(0);
                nowDate.setMinutes(0);
                nowDate.setSeconds(0);
                Date dueDate = documentTask.getDueDate();
                if (dueDate!=null){
                    dueDate.setHours(23);
                    dueDate.setMinutes(59);
                }
                Calendar calendar1 = Calendar.getInstance();
                calendar1.add(Calendar.DATE, 1);
                Date lessDate =calendar1.getTime();
                lessDate.setHours(0);
                lessDate.setMinutes(0);
                lessDate.setSeconds(0);

                if (dueDate!=null && documentTask.getStatus()!=null
                        && !documentTask.getStatus().equals(TaskStatus.Checking.getId())
                        && !documentTask.getStatus().equals(TaskStatus.Complete.getId())
                ){
//                    System.out.println("now date==" + nowDate);
//                    System.out.println("less date==" + lessDate);
//                    System.out.println("due date==" + dueDate);
                    if ((dueDate.before(lessDate) || dueDate.equals(lessDate))
                            && (dueDate.after(nowDate) || dueDate.equals(nowDate))
                    ) {
                        statisticInner.setLessDeadlineCount(statisticInner.getLessDeadlineCount()+1);
//                        System.out.println("less id=" + documentTaskSub.getId() + "  due=" + Common.uzbekistanDateFormat.format(dueDate));
                    }else if(dueDate.before(nowDate)){
//                        System.out.println("greater id=" + documentTaskSub.getId() + "  due=" + Common.uzbekistanDateFormat.format(dueDate));
                        statisticInner.setGreaterDeadlineCount(statisticInner.getGreaterDeadlineCount()+1);
                    }

                }
            }
        }
        return statisticInner;
    }

    @Override
    public ResolutionDTO resolutionCreateByTaskId(Integer taskId,String locale) {
        ResolutionDTO resolutionDTO = new ResolutionDTO();
        DocumentTask documentTask = getById(taskId);
        if (documentTask==null || documentTask.getDocumentId()==null) return resolutionDTO;
        Document document = documentTask.getDocument();
        if (document==null) return resolutionDTO;
        if (document.getOrganization()!=null){
            resolutionDTO.setOrganizationName(document.getOrganization().getNameTranslation(locale));
        }
        if (documentTask.getChiefId()!=null){
            resolutionDTO.setControlUser(documentTask.getChief().getShortName());
        }
        resolutionDTO.setExecuteFormName(document.getExecuteForm()!=null?helperService.getTranslation(document.getExecuteForm().getName(),locale):"");
        resolutionDTO.setContent(documentTask.getContent());
        List<DocumentTaskSub> documentTaskSubList = taskSubService.getListByDocIdAndTaskId(document.getId(),documentTask.getId());
        String performers = "";
        String controls = "";
        HashMap<Integer,String> performerMap = new HashMap<>();
        for (DocumentTaskSub documentTaskSub:documentTaskSubList){
            if (documentTaskSub.getReceiverId()!=null && !performerMap.containsKey(documentTaskSub.getReceiverId())){
                performerMap.put(documentTaskSub.getReceiverId(),"");
                if (documentTaskSub.getType().equals(TaskSubType.Control.getId())){
                    controls+=documentTaskSub.getReceiver().getShortName()+",";
                }else{
                    performers+=documentTaskSub.getReceiver().getShortName()+",";
                }
            }


        }
        resolutionDTO.setPerformers(!performers.isEmpty()?performers.substring(0,performers.length()-1):"");
        resolutionDTO.setControlUser(!controls.isEmpty()?controls.substring(0,controls.length()-1):"");
        User manager = userService.findById(document.getManagerId());
        if (manager!=null){
            resolutionDTO.setManagerName(manager.getFullName());
            resolutionDTO.setManagerPosition(manager.getPositionId()!=null?manager.getPosition().getNameTranslation(locale):"");
        }
        resolutionDTO.setRegistrationNumber(document.getRegistrationNumber());
        resolutionDTO.setRegistrationDate(document.getRegistrationDate()!=null?Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"");
        resolutionDTO.setDueDate(documentTask.getDueDate()!=null?Common.uzbekistanDateFormat.format(documentTask.getDueDate()):"");
        return resolutionDTO;
    }

    private static Specification<DocumentTask> getSpecification(
            final Integer organizationId,
            final Integer documentTypeId,
            final IncomingRegFilter incomingRegFilter,
            final Date deadlineDateBegin,
            final Date deadlineDateEnd,
            final Set<Integer> statuses,
            final Integer departmentId,
            final Integer receiverId,
            final Boolean specialControll
    ) {
        return (Specification<DocumentTask>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            if (organizationId != null) {
                //tashkilotga tegishli xatlar
                predicates.add(criteriaBuilder.equal(root.get("document").get("organizationId"), organizationId));
            }
            if (documentTypeId != null) {
                //kiruvchi, ichki hujjatlar
                predicates.add(criteriaBuilder.equal(root.get("document").get("documentTypeId"), documentTypeId));
            }
            if (incomingRegFilter.getDocumentOrganizationId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.join("document").join("documentSubs").get("organizationId"),
                        incomingRegFilter.getDocumentOrganizationId()
                ));
            }

            if (StringUtils.trimToNull(incomingRegFilter.getDocRegNumber()) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("docRegNumber")), "%" + incomingRegFilter.getDocRegNumber().toLowerCase() + "%"));
            }
            if (StringUtils.trimToNull(incomingRegFilter.getRegistrationNumber()) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("registrationNumber")), "%" + incomingRegFilter.getRegistrationNumber().toLowerCase() + "%"));
            }

            if (incomingRegFilter.getDateBegin() != null && incomingRegFilter.getDateEnd() == null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), incomingRegFilter.getDateBegin()));
            }
            if (incomingRegFilter.getDateEnd() != null && incomingRegFilter.getDateBegin() == null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), incomingRegFilter.getDateEnd()));
            }
            if (incomingRegFilter.getDateBegin() != null && incomingRegFilter.getDateEnd() != null) {
                predicates.add(criteriaBuilder.between(root.get("document").get("registrationDate").as(Date.class), incomingRegFilter.getDateBegin(), incomingRegFilter.getDateEnd()));
            }
            if (StringUtils.trimToNull(incomingRegFilter.getContent()) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("content")), "%" + incomingRegFilter.getContent().toLowerCase() + "%"));
            }
            if (StringUtils.trimToNull(incomingRegFilter.getTaskContent()) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("content")), "%" + incomingRegFilter.getTaskContent() + "%"));
            }
            if (incomingRegFilter.getPerformerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("task").get("performerId"), incomingRegFilter.getPerformerId()));
            }
            if (incomingRegFilter.getInsidePurpose() != null) {
                predicates.add(criteriaBuilder.equal(root.get("document").get("insidePurpose"), incomingRegFilter.getInsidePurpose()));
            }
            /*if (taskSubType != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (taskSubStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), taskSubStatus));
            }*/


            if (deadlineDateBegin != null && deadlineDateEnd == null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate").as(Date.class), deadlineDateBegin));
            }
            if (deadlineDateEnd != null && deadlineDateBegin == null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate").as(Date.class), deadlineDateEnd));
            }
            if (deadlineDateBegin != null && deadlineDateEnd != null) {
                predicates.add(criteriaBuilder.between(root.get("dueDate").as(Date.class), deadlineDateBegin, deadlineDateEnd));
            }
            if (statuses != null) {
                predicates.add(criteriaBuilder.in(root.get("status")).value(statuses));
            }
            if (departmentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("document").get("departmentId"), departmentId));
            }
            if (receiverId != null) {
                predicates.add(criteriaBuilder.equal(root.get("chiefId"), receiverId));
            }

            if (specialControll != null) {
                predicates.add(criteriaBuilder.equal(root.get("document").get("specialControll"),specialControll));
            }

            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    private static Specification<DocumentTask> getSpecificationReference(
            final Integer organizationId,
            final Integer documentTypeId,

            final ReferenceRegFilterDTO referenceRegFilterDTO,

            final Date deadlineDateBegin,
            final Date deadlineDateEnd,
            final Integer type,
            final Set<Integer> statuses,
            final Integer departmentId,
            final Integer receiverId,
            final Boolean specialControll
    ) {
        return (Specification<DocumentTask>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            System.out.println(referenceRegFilterDTO.getRegistrationNumber() + "  -");
            if (organizationId != null) {
                //tashkilotga tegishli xatlar
                predicates.add(criteriaBuilder.equal(root.get("document").get("organizationId"), organizationId));
            }
            if (documentTypeId != null) {
                //kiruvchi, chiquvchi, ichki hujjatlar
                predicates.add(criteriaBuilder.equal(root.get("document").get("documentTypeId"), documentTypeId));
            }

            if (referenceRegFilterDTO.getDocumentOrganizationId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.join("document").join("documentSubs").get("organizationId"),
                        referenceRegFilterDTO.getDocumentOrganizationId()
                ));
            }

            if (StringUtils.trimToNull(referenceRegFilterDTO.getDocRegNumber()) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("docRegNumber")), "%" + referenceRegFilterDTO.getDocRegNumber().toLowerCase() + "%"));
            }
            if (StringUtils.trimToNull(referenceRegFilterDTO.getRegistrationNumber()) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("registrationNumber")), "%" + referenceRegFilterDTO.getRegistrationNumber().toLowerCase() + "%"));
            }

            if (referenceRegFilterDTO.getDateBegin() != null && referenceRegFilterDTO.getDateEnd() == null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), referenceRegFilterDTO.getDateBegin()));
            }
            if (referenceRegFilterDTO.getDateEnd() != null && referenceRegFilterDTO.getDateBegin() == null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), referenceRegFilterDTO.getDateEnd()));
            }
            if (referenceRegFilterDTO.getDateBegin() != null && referenceRegFilterDTO.getDateEnd() != null) {
                predicates.add(criteriaBuilder.between(root.get("document").get("registrationDate").as(Date.class), referenceRegFilterDTO.getDateBegin(), referenceRegFilterDTO.getDateEnd()));
            }
            if (StringUtils.trimToNull(referenceRegFilterDTO.getContent()) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("content")), "%" + referenceRegFilterDTO.getContent().toLowerCase() + "%"));
            }
            if (StringUtils.trimToNull(referenceRegFilterDTO.getTaskContent()) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("content")), "%" + referenceRegFilterDTO.getTaskContent() + "%"));
            }
            if (referenceRegFilterDTO.getPerformerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("task").get("performerId"), referenceRegFilterDTO.getPerformerId()));
            }
            if (referenceRegFilterDTO.getInsidePurpose() != null) {
                predicates.add(criteriaBuilder.equal(root.get("document").get("insidePurpose"), referenceRegFilterDTO.getInsidePurpose()));
            }
            /*if (taskSubType != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (taskSubStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), taskSubStatus));
            }*/


            if (deadlineDateBegin != null && deadlineDateEnd == null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate").as(Date.class), deadlineDateBegin));
            }
            if (deadlineDateEnd != null && deadlineDateBegin == null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate").as(Date.class), deadlineDateEnd));
            }
            if (deadlineDateBegin != null && deadlineDateEnd != null) {
                predicates.add(criteriaBuilder.between(root.get("dueDate").as(Date.class), deadlineDateBegin, deadlineDateEnd));
            }

            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (statuses != null) {
                predicates.add(criteriaBuilder.in(root.get("status")).value(statuses));
            }
            if (departmentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("departmentId"), departmentId));
            }
            if (receiverId != null) {
                predicates.add(criteriaBuilder.equal(root.get("receiverId"), receiverId));
            }

            if (specialControll != null) {
                predicates.add(criteriaBuilder.equal(root.get("document").get("specialControll"),specialControll));
            }

            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<String> getColorAndText(Long intervalHours,String locale){
        Integer intervalDate = (int) Math.abs(intervalHours/24);
        String colorText="";
        String dueText="";
        String soatName = helperService.getTranslation("sys_hours",locale);
        String kunName = helperService.getTranslation("sys_day",locale);
        List<String> result = new ArrayList<>();

        if (intervalHours<48){
            colorText+=" text-danger ";
            if (intervalHours<0){
                dueText= helperService.getTranslation("due_text.passed",locale)+ ": ";
                dueText+=intervalDate!=0?Math.abs(intervalDate)+" "+kunName:Math.abs(intervalHours)+" "+soatName;
            }else{
                dueText=helperService.getTranslation("due_text.left",locale)+ ": ";
                dueText+=intervalDate!=0?intervalDate.toString()+" "+kunName:intervalHours.toString()+" "+soatName;
            }
        }else if (intervalHours <=72){
            dueText=helperService.getTranslation("due_text.left",locale)+ ": ";
            dueText+=intervalDate!=0?intervalDate.toString()+" "+kunName:intervalHours.toString()+" "+soatName;
            colorText+=" text-warning ";
        }

        if (intervalHours>72 && intervalHours<=96){
            dueText=helperService.getTranslation("due_text.left",locale)+ ": ";
            dueText+=intervalDate!=0?intervalDate.toString()+" "+kunName:intervalHours.toString()+" "+soatName;
        }
        result.add(colorText);
        result.add(dueText);

        return result;
    }

    public Long countAllTasksByDocumentTypeId(Integer organizationId, Integer departmentId, Integer documentTypeId){
        return taskRepository.countAllDocumentTaskByDocumentType(organizationId, departmentId, documentTypeId);
    }

    public Long countAllTasksByDocumentTypeIdAndDueDateBetween(Integer organizationId, Integer departmentId, Integer documentTypeId, Date dateBegin, Date dateEnd){
        return taskRepository.countAllDocumentTaskByDocumentTypeAndDueDateBetween(organizationId, departmentId, documentTypeId, dateBegin, dateEnd);
    }

    public Long countAllTasksByDocumentTypeIdAndDueDateBefore(Integer organizationId, Integer departmentId, Integer documentTypeId, Date date){
        return taskRepository.countAllDocumentTaskByDocumentTypeAndDueDateBefore(organizationId, departmentId, documentTypeId, date);
    }

    public Long countAllTasksByDocumentTypeIdAndTaskStatus(Integer organizationId, Integer departmentId, Integer documentTypeId, Integer status){
        return taskRepository.countAllDocumentTaskByDocumentTypeIdAndStatus(organizationId, departmentId, documentTypeId, status);
    }
}
