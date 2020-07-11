package uz.maroqand.ecology.docmanagement.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.telegram.SendQueryType;
import uz.maroqand.ecology.core.constant.user.NotificationType;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.service.user.NotificationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.constant.DocUrls;
import uz.maroqand.ecology.docmanagement.constant.TaskSubStatus;
import uz.maroqand.ecology.docmanagement.dto.StaticInnerInTaskSubDto;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentTask;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskSubRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentSubService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * Created by Utkirbek Boltaev on 15.02.2020.
 * (uz)
 */

@Service
public class DocumentTaskSubServiceImpl implements DocumentTaskSubService {

    private final DocumentTaskSubRepository documentTaskSubRepository;
    private final DocumentSubService documentSubService;
    private final HelperService helperService;
    private final DocumentService documentService;
    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public DocumentTaskSubServiceImpl(DocumentTaskSubRepository documentTaskSubRepository, DocumentSubService documentSubService, HelperService helperService, DocumentService documentService, NotificationService notificationService, UserService userService) {
        this.documentTaskSubRepository = documentTaskSubRepository;
        this.documentSubService = documentSubService;
        this.helperService = helperService;
        this.documentService = documentService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @Override
    public DocumentTaskSub getById(Integer id) {
        return documentTaskSubRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public Integer getCountByStatus() {
        Set<Integer> statuses = new LinkedHashSet<>();
        statuses.add(TaskSubStatus.ForChangePerformer.getId());
        return documentTaskSubRepository.countByStatus(statuses);
    }

    @Override
    public DocumentTaskSub update(DocumentTaskSub taskSub) {
        taskSub.setUpdateAt(new Date());
        return documentTaskSubRepository.save(taskSub);
    }

    @Override
    public String buildTree(Integer level,List<DocumentTaskSub> documentTaskSubList, Map<Integer,DocumentTaskSub> taskSubMap,String locale){
        String tree="";
        for (DocumentTaskSub taskSub: documentTaskSubList) {
            if (taskSub.getLevel().equals(level) && !taskSubMap.containsKey(taskSub.getId())){
                taskSubMap.put(taskSub.getId(),taskSub);
                String getReceiverName =  helperService.getUserFullNameById(taskSub.getReceiverId()) + " (" + helperService.getPositionName(taskSub.getReceiver().getPositionId(),locale)+")";
                tree="[{text:\"" + getReceiverName + "\", tags:" + taskSub.getId();
                List<DocumentTaskSub> taskSubs = getListByTaskIdAndLevel(taskSub.getTaskId(),(level+1));
                if (taskSubs.size()>0){
                    tree+= ",nodes:" + buildTree((level+1),documentTaskSubList,taskSubMap,locale) + "}]";
                }else{
                    tree+="}]";
                }
            }
        }
        return tree;
    }



    @Override
    public List<DocumentTaskSub> getListByDocId(Integer docId) {
        return documentTaskSubRepository.findByDocumentIdAndDeletedFalseOrderByIdAsc(docId);
    }

    @Override
    public List<DocumentTaskSub> getListByTaskIdAndLevel(Integer taskId, Integer level) {
        return documentTaskSubRepository.findByTaskIdAndLevelAndDeletedFalse(taskId,level);
    }

    @Override
    public void allTaskSubCompleteGetTaskId(DocumentTask task, Integer userId) {
        List<DocumentTaskSub> documentTaskSubList = documentTaskSubRepository.findByTaskIdAndDeletedFalse(task.getId());
        Document document = documentService.getById(task.getDocumentId());
        for (DocumentTaskSub documentTaskSub:documentTaskSubList) {
            documentTaskSub.setStatus(TaskSubStatus.Complete.getId());
            update(documentTaskSub);
            String url = getUrl(document,documentTaskSub.getId());

            notificationService.create(
                    documentTaskSub.getReceiverId(),
                    NotificationType.Document,
                    "doc_notification.check_complete",
                    document.getRegistrationNumber(),
                    "doc_notification_message.check_complete",
                    url,
                    userId
            );
        }

    }

    @Override
    public void allTaskSubRejectedGetTaskId(DocumentTask task,Integer userId) {
        List<DocumentTaskSub> documentTaskSubList = documentTaskSubRepository.findByTaskIdAndDeletedFalse(task.getId());
        Document document = documentService.getById(task.getDocumentId());
        for (DocumentTaskSub documentTaskSub:documentTaskSubList) {
            documentTaskSub.setStatus(TaskSubStatus.Rejected.getId());
            update(documentTaskSub);
            String url = getUrl(document,documentTaskSub.getId());

            notificationService.create(
                    documentTaskSub.getReceiverId(),
                    NotificationType.Document,
                    "doc_notification.check_rejected",
                    document.getRegistrationNumber(),
                    "doc_notification_message.check_rejected",
                    url,
                    userId
            );
        }
    }

    @Override
    public List<DocumentTaskSub> getListByDocIdAndTaskId(Integer docId,Integer taskId) {
        return documentTaskSubRepository.findByDocumentIdAndTaskIdAndDeletedFalseOrderByIdAsc(docId,taskId);
    }

    @Override
    public DocumentTaskSub createNewSubTask(Integer level, Document document, Integer taskId, String content, Date dueDate, Integer type, Integer senderId, Integer receiverId, Integer departmentId) {
        DocumentTaskSub documentTaskSub = new DocumentTaskSub();
        documentTaskSub.setDocumentId(document.getId());
        documentTaskSub.setTaskId(taskId);
        documentTaskSub.setContent(content.trim());
        documentTaskSub.setDueDate(dueDate);
        documentTaskSub.setType(type);
        documentTaskSub.setStatus(TaskSubStatus.New.getId());
        documentTaskSub.setSenderId(senderId);
        documentTaskSub.setReceiverId(receiverId);
        documentTaskSub.setDepartmentId(departmentId);
        documentTaskSub.setDeleted(Boolean.FALSE);
        documentTaskSub.setCreatedAt(new Date());
        documentTaskSub.setCreatedById(senderId);
        documentTaskSub.setLevel(level!=null?(level+1):1);
        documentTaskSub = documentTaskSubRepository.save(documentTaskSub);
        String url = getUrl(document,documentTaskSub.getId());

        notificationService.create(
                documentTaskSub.getReceiverId(),
                NotificationType.Document,
                "doc_notification.new",
                document.getRegistrationNumber(),
                "doc_notification_message.new",
                url,
                senderId
        );

//        bot.sendMsg(256161586,"yangi keldi");

        return documentTaskSub;
    }

    @Override
    public Page<DocumentTaskSub> findFilter(Integer organizationId, Integer documentOrganizationId, String docRegNumber, String registrationNumber, Date dateBegin, Date dateEnd, String taskContent, String content, Integer performerId, Integer taskSubType, Integer taskSubStatus, Date deadlineDateBegin, Date deadlineDateEnd, Integer type, Set<Integer> status, Integer departmentId, Integer receiverId, Boolean specialControll, Pageable pageable) {
        return documentTaskSubRepository.findAll(getSpestification(
                organizationId,
                documentOrganizationId, docRegNumber, registrationNumber, dateBegin, dateEnd, taskContent, content, performerId, taskSubType, taskSubStatus,
                deadlineDateBegin, deadlineDateEnd, type, status, departmentId, receiverId,specialControll), pageable);

    }

    @Override
    public Page<DocumentTaskSub> findFiltered(
            Integer organizationId,
            List<Integer> documentTypeIds,

            Integer documentOrganizationId,
            String docRegNumber,
            String registrationNumber,
            Date dateBegin,
            Date dateEnd,
            String taskContent,
            String content,
            Integer performerId,
            Integer taskSubType,
            Integer taskSubStatus,

            Date deadlineDateBegin,
            Date deadlineDateEnd,
            Integer type,
            Set<Integer> status,
            Integer departmentId,
            Integer receiverId,
            Boolean specialControll,
            Pageable pageable
    ) {
        return documentTaskSubRepository.findAll(getSpecification(
                organizationId, documentTypeIds,
                documentOrganizationId, docRegNumber, registrationNumber, dateBegin, dateEnd, taskContent, content, performerId, taskSubType, taskSubStatus,
                deadlineDateBegin, deadlineDateEnd, type, status, departmentId, receiverId,specialControll), pageable);
    }

    private Specification<DocumentTaskSub> getSpecification(
            final Integer organizationId,
            final List<Integer> documentTypeIds,

            final Integer documentOrganizationId,
            final String docRegNumber,
            final String registrationNumber,
            final Date dateBegin,
            final Date dateEnd,
            final String taskContent,
            final String content,
            final Integer performerId,
            final Integer taskSubType,
            final Integer taskSubStatus,

            final Date deadlineDateBegin,
            final Date deadlineDateEnd,
            final Integer type,
            final Set<Integer> statuses,
            final Integer departmentId,
            final Integer receiverId,
            final Boolean specialControll
    ) {
        return (Specification<DocumentTaskSub>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            if (organizationId != null) {
                //ushbu tashkilotga tegishli hujjatlar chiqishi uchun, user boshqa organizationga o'tsa eskisi ko'rinmaydi
                predicates.add(criteriaBuilder.equal(root.get("document").get("organizationId"), organizationId));
            }
            if (documentOrganizationId != null) {
                predicates.add(criteriaBuilder.equal(root.join("document").join("documentSubs").get("organizationId"), documentOrganizationId));
            }
            if (documentTypeIds != null) {
                //kiruvchi va chiquvchi hujjatlar ajratish uchun
                List<Predicate> p = new ArrayList<>(documentTypeIds.size());
                for (Integer documentTypeId : documentTypeIds){
                    if (documentTypeId != 2)
                        p.add(criteriaBuilder.equal(root.get("document").get("documentTypeId"), documentTypeId));
                }
                predicates.add(criteriaBuilder.or(p.toArray(new Predicate[0])));
            }

            if (StringUtils.trimToNull(docRegNumber) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("docRegNumber")), "%" + docRegNumber.toLowerCase() + "%"));
            }
            if (StringUtils.trimToNull(registrationNumber) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("registrationNumber")), "%" + registrationNumber.toLowerCase() + "%"));
            }

            if(dateEnd != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateEnd);
                calendar.add(Calendar.DATE, 1);
                predicates.add(criteriaBuilder.lessThan(root.get("document").get("registrationDate"), calendar.getTime()));
            }if(dateBegin != null)
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("document").get("registrationDate"), dateBegin));

            if (StringUtils.trimToNull(content) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("content")), "%" + content.toLowerCase() + "%"));
            }

            if (StringUtils.trimToNull(taskContent) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("content")), "%" + taskContent.toLowerCase() + "%"));
            }
            if (performerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("task").get("performerId"), performerId));
            }
            if (taskSubType != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (taskSubStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), taskSubStatus));
            }


            if (deadlineDateBegin != null && deadlineDateEnd == null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate").as(Date.class), deadlineDateBegin));//katta yoki teng
            }
            if (deadlineDateEnd != null && deadlineDateBegin == null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate").as(Date.class), deadlineDateEnd));//kichik yoki teng
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

    private static Specification<DocumentTaskSub> getSpestification(
            final Integer organizationId,

            final Integer documentOrganizationId,
            final String docRegNumber,
            final String registrationNumber,
            final Date dateBegin,
            final Date dateEnd,
            final String taskContent,
            final String content,
            final Integer performerId,
            final Integer taskSubType,
            final Integer taskSubStatus,

            final Date deadlineDateBegin,
            final Date deadlineDateEnd,
            final Integer type,
            final Set<Integer> statuses,
            final Integer departmentId,
            final Integer receiverId,
            final Boolean specialControll
    ) {
        return (Specification<DocumentTaskSub>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            if (organizationId != null) {
                //ushbu tashkilotga tegishli hujjatlar chiqishi uchun, user boshqa organizationga o'tsa eskisi ko'rinmaydi
                predicates.add(criteriaBuilder.equal(root.get("document").get("organizationId"), organizationId));
            }
            if (documentOrganizationId != null) {
                predicates.add(criteriaBuilder.equal(root.join("document").join("documentSubs").get("organizationId"), documentOrganizationId));
            }

            if (StringUtils.trimToNull(docRegNumber) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("docRegNumber")), "%" + docRegNumber.toLowerCase() + "%"));
            }
            if (StringUtils.trimToNull(registrationNumber) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("registrationNumber")), "%" + registrationNumber.toLowerCase() + "%"));
            }

            if (dateBegin != null && dateEnd == null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), dateBegin));
            }
            if (dateEnd != null && dateBegin == null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("document").get("registrationDate").as(Date.class), dateEnd));
            }
            if (dateBegin != null && dateEnd != null) {
                predicates.add(criteriaBuilder.between(root.get("document").get("registrationDate").as(Date.class), dateBegin, dateEnd));
            }
            if (StringUtils.trimToNull(content) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("document").<String>get("content")), "%" + content.toLowerCase() + "%"));
            }

            if (StringUtils.trimToNull(taskContent) != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("content")), "%" + taskContent.toLowerCase() + "%"));
            }
            if (performerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("task").get("performerId"), performerId));
            }
            if (taskSubType != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (taskSubStatus != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), taskSubStatus));
            }

            if (deadlineDateBegin != null && deadlineDateEnd == null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate").as(Date.class), deadlineDateBegin));//katta yoki teng
            }
            if (deadlineDateEnd != null && deadlineDateBegin == null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate").as(Date.class), deadlineDateEnd));//kichik yoki teng
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


    //statistics
    @Override
    public Integer countByReceiverIdAndDueDateGreaterThanEqual(Integer receiverId, Date date){
        return documentTaskSubRepository.countByReceiverIdAndDueDateGreaterThanEqual(receiverId, date);
    }

    @Override
    public Integer countByReceiverIdAndDueDateLessThanEqual(Integer receiverId, Date date){
        return documentTaskSubRepository.countByReceiverIdAndDueDateLessThanEqual(receiverId, date);
    }

    @Override
    public Integer countAllByStatusAndDepartmentId(Integer status, Integer departmentId) {
        return documentTaskSubRepository.countAllByStatusAndDepartmentId(status,departmentId);
    }

    @Override
    public Integer countAllByStatusAndDate(Date date, Integer departmentId) {
        int number=0;
        Date nowDate = new Date();
        date.setHours(23);
        date.setMinutes(59);
        Long intervalHours = (date.getTime()-nowDate.getTime())/3600000;
        System.out.println(intervalHours);
        if(intervalHours<72&&intervalHours>0){
            number=documentTaskSubRepository.countAllByStatusAndDepartmentId(null,departmentId);
        }else if(intervalHours<0){
            number=documentTaskSubRepository.countAllByStatusAndDepartmentId(null,departmentId);
        }

        return number;
    }

    @Override
    public Integer countByReceiverIdAndStatusIn(Integer receiverId, Set<Integer> statuses){
        return documentTaskSubRepository.countByReceiverIdAndStatusIn(receiverId, statuses);
    }

    @Override
    public Integer countByReceiverId(Integer receiverId){
        return documentTaskSubRepository.countByReceiverId(receiverId);
    }

    @Override
    public StaticInnerInTaskSubDto countAllByTypeAndReceiverId(Integer documentTypeId, Integer receiverId) {
        StaticInnerInTaskSubDto statisticInner = new StaticInnerInTaskSubDto();
        if (receiverId==null) return statisticInner;
        List<DocumentTaskSub> documentTaskSubList = documentTaskSubRepository.findByReceiverIdAndDeletedFalseOrderByIdAsc(receiverId);
        for (DocumentTaskSub documentTaskSub: documentTaskSubList){
            if (documentTaskSub.getDocument().getDocumentTypeId().equals(documentTypeId)){//todo vaqtincha to'g'rilash kerak
                statisticInner.setAllCount(statisticInner.getAllCount()+1);                         //barcha ichki xatlar

                if (documentTaskSub.getStatus()!=null){
                    if (documentTaskSub.getStatus().equals(TaskSubStatus.New.getId())
                            || documentTaskSub.getStatus().equals(TaskSubStatus.Initial.getId())){
                        statisticInner.setNewCount(statisticInner.getNewCount()+1);                     //yangi
                    }

                    if (documentTaskSub.getStatus().equals(TaskSubStatus.InProgress.getId())
                            || documentTaskSub.getStatus().equals(TaskSubStatus.Waiting.getId())
                            || documentTaskSub.getStatus().equals(TaskSubStatus.Agreement.getId())){
                        statisticInner.setInProgressCount(statisticInner.getInProgressCount()+1);       //jarayondagilar
                    }

                    if (documentTaskSub.getStatus().equals(TaskSubStatus.Checking.getId())){
                        statisticInner.setCheckingCount(statisticInner.getCheckingCount()+1);                     //ijro etilganlar
                    }

                }

//                Date nowDate = new Date();
//                nowDate.setHours(0);
//                nowDate.setMinutes(0);
//                nowDate.setSeconds(0);
//                Date dueDate = documentTaskSub.getDueDate();
//                if (dueDate!=null){
//                    dueDate.setHours(23);
//                    dueDate.setMinutes(59);
//                }
//                Calendar calendar1 = Calendar.getInstance();
//                calendar1.add(Calendar.DATE, 1);
//                Date lessDate =calendar1.getTime();
//                lessDate.setHours(0);
//                lessDate.setMinutes(0);
//                lessDate.setSeconds(0);
//
//                if (dueDate!=null && documentTaskSub.getStatus()!=null
//                        && !documentTaskSub.getStatus().equals(TaskSubStatus.Checking.getId())
//                            && !documentTaskSub.getStatus().equals(TaskSubStatus.Complete.getId())
//                ){
//                    if ((dueDate.before(lessDate) || dueDate.equals(lessDate))
//                            && (dueDate.after(nowDate) || dueDate.equals(nowDate))
//                    ) {
//                    statisticInner.setLessDeadlineCount(statisticInner.getLessDeadlineCount()+1);
//
//                    }else if(dueDate.before(nowDate)){
//
//                        statisticInner.setGreaterDeadlineCount(statisticInner.getGreaterDeadlineCount()+1);
//                    }
//
//                }
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                Date currentDate = calendar.getTime();
                calendar.add(Calendar.DAY_OF_MONTH, -2);
                Date nearestDate = calendar.getTime();

                Date currentDocumentTaskSubDueDate = documentTaskSub.getDueDate();

                if(currentDocumentTaskSubDueDate!=null && currentDocumentTaskSubDueDate.before(currentDate))
                    statisticInner.setGreaterDeadlineCount(statisticInner.getGreaterDeadlineCount()+1);
                if(currentDate.before(nearestDate) && currentDate.after(currentDate))
                    statisticInner.setLessDeadlineCount(statisticInner.getLessDeadlineCount()+1);

            }
        }
        return statisticInner;
    }

    @Override
    public Integer countByReceiverIdAndStatus(Integer receiverId, Set<Integer> statuses) {
        return documentTaskSubRepository.countByReceiverIdAndStatus(receiverId,statuses);
    }

    @Override
    public Integer countByReceiverIdAll(Integer receiverId) {
        return documentTaskSubRepository.countByReceiverIdAll(receiverId);
    }

    @Override
    public Integer countByReceiverIdAndDueDateLessThanEqualFor(Integer receiverId,Date now) {
        return documentTaskSubRepository.countByReceiverIdAndDueDateLessThanEqualFor(receiverId,now);
    }

    @Override
    public String getUrl(Document document,Integer  taskSubId){
        String url="";

        switch (document.getDocumentTypeId()){
            case 1:
                url= DocUrls.IncomingView+"?id="+taskSubId;
                break;//Kirish
            case 2:
                url= DocUrls.OutgoingView+"?id="+taskSubId;
                break;//Chiqish
            case 3:
                url= DocUrls.InnerView+"?id="+taskSubId;
                break;//ichki

            case 4:
                url= DocUrls.ReferenceView+"?id="+taskSubId;
                break;//murojaat
            default:
                url= DocUrls.Dashboard;
                break;
        }
        return url;
    }

    @Override
    public List<DocumentTaskSub> getAllByReceiverIdAndStatuses(Integer receiverId, Set<Integer> statusSet) {
        return documentTaskSubRepository.findByReceiverIdAndStatusInAndDeletedFalseOrderByIdAsc(receiverId,statusSet);
    }

    @Override
    public String getMessageText(Integer telegramUserId, SendQueryType sendQueryType) {
        System.out.println("getMessageText  " + sendQueryType + "  telId=" + telegramUserId);
        User user = userService.getByTelegramId(telegramUserId);
        if (user==null){
            return " user null  " + telegramUserId;
        }
        switch (sendQueryType){
            case All:
                return allDocument(user.getId());
            case NewDocument:
                return newDocument(user.getId());
            case InProccess:
                return inProcessDocument(user.getId());
            case DueDate:
                return dueDateDocument(user.getId());
            case ExecutiveDate:
                return executiveDocument(user.getId());
            default: return " ";
        }
    }

    private String allDocument(Integer userId){
        String text = "";
        text+=newDocument(userId)+"\n";
        text+=inProcessDocument(userId)+"\n";
        text+=dueDateDocument(userId)+"\n";
        text+=executiveDocument(userId);
        return text;
    }

    private String newDocument(Integer userId){
        Set<Integer> subStatusSet = new HashSet<>();
        subStatusSet.add(TaskSubStatus.New.getId());
        List<DocumentTaskSub> taskSubList = getAllByReceiverIdAndStatuses(userId,subStatusSet);
        Integer listSize=taskSubList.size();
        StringBuilder text = new StringBuilder("Yangi xujjat soni:= " + listSize + "\n");
        for (DocumentTaskSub taskSub: taskSubList) {
            if (taskSub.getDocumentId()!=null){
                Document document = documentService.getById(taskSub.getDocumentId());
                text.append(document!=null?"xujjat raqami: "+document.getRegistrationNumber():"  ---").append("\n");
            }
        }
     return text.toString();
    }

    private String inProcessDocument(Integer userId){
        Set<Integer> subStatusSet = new HashSet<>();
        subStatusSet.add(TaskSubStatus.InProgress.getId());
        List<DocumentTaskSub> taskSubList = getAllByReceiverIdAndStatuses(userId,subStatusSet);
        Integer listSize=taskSubList.size();
        StringBuilder text = new StringBuilder("Jarayondagi xujjat soni:= " + listSize + "\n");
        for (DocumentTaskSub taskSub: taskSubList) {
            if (taskSub.getDocumentId()!=null){
                Document document = documentService.getById(taskSub.getDocumentId());
                text.append(document!=null?"xujjat raqami: "+document.getRegistrationNumber():"  ---").append("\n");
            }
        }
        return text.toString();
    }

    private String dueDateDocument(Integer userId){
        Set<Integer> subStatusSet = new HashSet<>();
        subStatusSet.add(TaskSubStatus.New.getId());
        subStatusSet.add(TaskSubStatus.InProgress.getId());
        subStatusSet.add(TaskSubStatus.Waiting.getId());
        subStatusSet.add(TaskSubStatus.Agreement.getId());
        List<DocumentTaskSub> taskSubList = getAllByReceiverIdAndStatuses(userId,subStatusSet);
        Integer listSize=0;
        StringBuilder text = new StringBuilder();
        for (DocumentTaskSub taskSub: taskSubList) {
            if (taskSub.getDocumentId()!=null && taskSub.getDueDate()!=null){
                Date taskDueDate = taskSub.getDueDate();
                taskDueDate.setHours(23);
                taskDueDate.setMinutes(59);
                Calendar cd = Calendar.getInstance();
                cd.add(Calendar.DATE,3);
                Date nowdate = new Date();
                if (taskDueDate.after(nowdate) && taskDueDate.before(cd.getTime())){
                    listSize++;
                    Document document = documentService.getById(taskSub.getDocumentId());
                    text.append(document != null ? "xujjat raqami: " + document.getRegistrationNumber() : "  ---").append("\n");
                }
            }
        }
        text.insert(0, "Muddati yaqinlashgan xujjat soni:= " + listSize + "\n");
        return text.toString();
    }

    private String executiveDocument(Integer userId){
        Set<Integer> subStatusSet = new HashSet<>();
        subStatusSet.add(TaskSubStatus.New.getId());
        subStatusSet.add(TaskSubStatus.InProgress.getId());
        subStatusSet.add(TaskSubStatus.Waiting.getId());
        subStatusSet.add(TaskSubStatus.Agreement.getId());
        List<DocumentTaskSub> taskSubList = getAllByReceiverIdAndStatuses(userId,subStatusSet);
        Integer listSize=0;
        StringBuilder text = new StringBuilder();
        for (DocumentTaskSub taskSub: taskSubList) {
            if (taskSub.getDocumentId()!=null && taskSub.getDueDate()!=null){
                Date taskDueDate = taskSub.getDueDate();
                Calendar cd = Calendar.getInstance();
                cd.add(Calendar.DATE,-1);
                if (taskDueDate.before(cd.getTime())){
                    listSize++;
                    Document document = documentService.getById(taskSub.getDocumentId());
                    text.append(document != null ? "xujjat raqami: " + document.getRegistrationNumber() : "  ---").append("\n");
                }
            }
        }
        text.insert(0, "Muddati tugagan xujjat soni:= " + listSize + "\n");
        return text.toString();
    }
}
