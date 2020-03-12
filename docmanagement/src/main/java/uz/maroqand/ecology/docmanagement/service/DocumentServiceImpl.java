package uz.maroqand.ecology.docmanagement.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.util.Common;
import uz.maroqand.ecology.core.util.DateParser;
import uz.maroqand.ecology.docmanagement.constant.*;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Document;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;
import uz.maroqand.ecology.docmanagement.repository.DocumentRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentSubService;
import uz.maroqand.ecology.docmanagement.service.interfaces.JournalService;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final JournalService journalService;
    private final DocumentSubService documentSubService;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository, JournalService journalService, DocumentSubService documentSubService) {
        this.documentRepository = documentRepository;
        this.journalService = journalService;
        this.documentSubService = documentSubService;
    }

    @Override
    public Document getById(Integer id) {
        return documentRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public String createTree(Document document) {
        Document topParent = getTopParentId(document);
        List<Document> childList = getListByAddDocId(topParent.getId());
        String tree = buildTree(document,childList);
        if (!document.getId().equals(topParent.getId())){
            StringBuilder parent = new StringBuilder();
            System.out.println(topParent.getId() + "  " + topParent.getDocumentTypeId());
            String icon;
            if (topParent.getDocumentTypeId()!=null &&  topParent.getDocumentType().getType().equals(DocumentTypeEnum.OutgoingDocuments.getId())){
                icon="\'fa fa-upload\'";
            }else{
                icon="\'fa fa-download\'";
            }
            String regDate = topParent.getRegistrationDate()!=null?Common.uzbekistanDateFormat.format(topParent.getRegistrationDate()):"";
            parent.append("{ text: \' <span id=\"node_text_").append(topParent.getId()).append("\"> ").append(topParent.getRegistrationNumber()).append(" (").append(regDate).append(")</span>\'").append(" , tags:").append(topParent.getId()).append(", icon:").append(icon);
            parent.append(",nodes : [").append(tree).append("]}");
            return "[" + parent.toString() + "]";
        }else{
        return "[" + tree + "]";
        }
    }

    @Override
    public String buildTree(Document documentOld,List<Document> documentList) {
        StringBuilder tree = new StringBuilder();
        String icon;
        String state;
        String button;

        if (documentList==null || documentList.size()==0){
            if (documentOld.getDocumentTypeId()!=null && documentOld.getDocumentType().getType().equals(DocumentTypeEnum.OutgoingDocuments.getId())){
                icon="\'fa fa-upload\'";
            }else{
                icon="\'fa fa-download\'";
            }
            state=", state: {checked: false,disabled: false,expanded: false,selected: true,open: true,}";
            String regDate = documentOld.getRegistrationDate()!=null?Common.uzbekistanDateFormat.format(documentOld.getRegistrationDate()):"";
            button="<button id=\"doc_view_button\" class=\"float-right close\" type=\"button\" onclick=\" getViewPage(" +documentOld.getId() +")\" ><i class=\"fas fa-arrow-right\"></i></button>";

            tree.append("{ text: \' <span id=\"node_text_").append(documentOld.getId()).append("\"> ").append(documentOld.getRegistrationNumber()).append(" (").append(regDate).append(")").append(button).append("</span>\' , tags:").append(documentOld.getId()).append(", icon:").append(icon).append(state);
                tree.append("}");

                return tree.toString();
        }

            for (Document document: documentList){
                if (documentOld.getDocumentTypeId()!=null && document.getDocumentTypeId()!=null &&  document.getDocumentType().getType().equals(DocumentTypeEnum.OutgoingDocuments.getId())){
                    icon="\'fa fa-upload\'";
                }else{
                    icon="\'fa fa-download\'";
                }
                if (document.getId().equals(documentOld.getId())){
//                    System.out.println("id==id " + document.getId() + "  == " + documentOld.getId());
                    state=", state: {checked: false,disabled: false,expanded: false,selected: true,open: true,}";
                    button="<button id=\"doc_view_button\" class=\"float-right close\" type=\"button\" onclick=\" getViewPage(" +documentOld.getId() +")\" ><i class=\"fas fa-arrow-right\"></i></button>";
                }else{
                    state="";
                    button="";
                }
                String regDate = document.getRegistrationDate()!=null?Common.uzbekistanDateFormat.format(document.getRegistrationDate()):"";
                tree.append("{ text: \' <span id=\"node_text_").append(document.getId()).append("\"> ").append(document.getRegistrationNumber()).append(" (").append(regDate).append(") ").append(button).append("</span>\' , tags:").append(document.getId()).append(", icon:").append(icon).append(state);
                List<Document> childList = getListByAddDocId(document.getId());
                if (childList.size()>0){
                    tree.append(", nodes:[").append(buildTree(documentOld, childList)).append("]},");
                }else{
                    tree.append("},");
                }
            }

        return tree.toString();
    }


    @Override
    public Document createDoc(Integer documentTypeId, Document document, User user) {
        document.setOrganizationId(user.getOrganizationId());
        document.setDocumentTypeId(documentTypeId);
        document.setStatus(DocumentStatus.New);
        if (document.getControlId()!=null){
            document.setSpecialControll(Boolean.TRUE);
        }else{
            document.setSpecialControll(Boolean.FALSE);
        }
        document.setRegistrationNumber(journalService.getRegistrationNumberByJournalId(document.getJournalId()));
        document.setRegistrationDate(new Date());

        document.setCreatedById(user.getId());
        document.setCreatedAt(new Date());
        document.setDeleted(Boolean.FALSE);
        return documentRepository.save(document);
    }
    @Override
    public Document createReference(Integer documentTypeId, Document document, User user) {
        document.setOrganizationId(user.getOrganizationId());
        document.setDocumentTypeId(documentTypeId);
        document.setStatus(DocumentStatus.New);

        document.setRegistrationDate(new Date());

        document.setCreatedById(user.getId());
        document.setCreatedAt(new Date());
        document.setDeleted(Boolean.FALSE);
        return documentRepository.save(document);
    }


    @Override
    public void update(Document document) {
        document.setUpdateAt(new Date());
        documentRepository.save(document);
    }

    @Override
    public Page<Document> findFiltered(
            DocFilterDTO filterDTO,
            Pageable pageable
    ) {
        return documentRepository.findAll(getSpesification(filterDTO), pageable);
    }

    private static Specification<Document> getSpesification(final DocFilterDTO filterDTO) {
        return (Specification<Document>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();

            if (filterDTO != null) {
                if (filterDTO.getDocumentId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), filterDTO.getDocumentId()));
                }

                if (filterDTO.getRegistrationNumber() != null) {
                    System.out.println(filterDTO.getRegistrationNumber());
                    predicates.add(criteriaBuilder.like(root.get("registrationNumber"), "%" + filterDTO.getRegistrationNumber() + "%"));
                }

                    Date dateBegin = DateParser.TryParse(filterDTO.getRegistrationDateBegin(), Common.uzbekistanDateFormat);
                    Date dateEnd = DateParser.TryParse(filterDTO.getRegistrationDateEnd(), Common.uzbekistanDateFormat);
                    if (dateBegin != null && dateEnd == null) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("registrationDate").as(Date.class), dateBegin));
                    }
                    if (dateEnd != null && dateBegin == null) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("registrationDate").as(Date.class), dateEnd));
                    }
                    if (dateBegin != null && dateEnd != null) {
                        predicates.add(criteriaBuilder.between(root.get("registrationDate").as(Date.class), dateBegin, dateEnd));
                    }

                    if (filterDTO.getControlCard() != null) {
                        predicates.add(criteriaBuilder.equal(root, filterDTO.getControlCard()));
                    }

                    if (filterDTO.getDocumentType() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("documentTypeId"), filterDTO.getDocumentType()));
                    }

                    if (filterDTO.getDocumentTypeEnum() != null) {
                        predicates.add(criteriaBuilder.equal(root.join("documentType").get("type"), filterDTO.getDocumentTypeEnum().getId()));
                    }

                    if (filterDTO.getCorrespondentType() != null) {
                        predicates.add(criteriaBuilder.equal(root, filterDTO.getCorrespondentType()));
                    }

                    if (filterDTO.getContent() != null) {
                        predicates.add(criteriaBuilder.like(root.join("documentDescription").<String>get("content"), "%" + filterDTO.getContent() + "%"));
                    }

                    if (filterDTO.getChief() != null) {
                        predicates.add(criteriaBuilder.equal(root, filterDTO.getChief()));
                    }

                    if (filterDTO.getExecutors() != null) {
                        predicates.add(criteriaBuilder.equal(root, filterDTO.getExecutors()));
                    }

                    if (filterDTO.getResolution() != null) {
                        predicates.add(criteriaBuilder.equal(root, filterDTO.getResolution()));
                    }

                    if (filterDTO.getExecutePath() != null) {
                        predicates.add(criteriaBuilder.equal(root, filterDTO.getExecutePath()));
                    }

                    if (filterDTO.getExecuteStatus() != null) {
                        predicates.add(criteriaBuilder.equal(root, filterDTO.getExecuteStatus()));
                    }

                    Date executeDateBegin = DateParser.TryParse(filterDTO.getExecuteDateBegin(), Common.uzbekistanDateFormat);
                    Date executeDateEnd = DateParser.TryParse(filterDTO.getRegistrationDateEnd(), Common.uzbekistanDateFormat);
                    if (executeDateBegin != null && executeDateEnd == null) {
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("registrationDate").as(Date.class), executeDateBegin));
                    }
                    if (executeDateEnd != null && executeDateBegin == null) {
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("registrationDate").as(Date.class), executeDateEnd));
                    }
                    if (executeDateBegin != null && executeDateEnd != null) {
                        predicates.add(criteriaBuilder.between(root.get("registrationDate").as(Date.class), executeDateBegin, executeDateEnd));
                    }

                    if (filterDTO.getExecuteControlStatus() != null) {
                        predicates.add(criteriaBuilder.equal(root, filterDTO.getExecuteControlStatus()));
                    }

                    if (filterDTO.getInsidePurposeStatus() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("insicePurpose"), filterDTO.getInsidePurposeStatus()));
                    }

                    if (filterDTO.getCoExecutorStatus() != null) {
                        predicates.add(criteriaBuilder.equal(root, filterDTO.getCoExecutorStatus()));
                    }

                    if (filterDTO.getReplies() != null) {
                        predicates.add(criteriaBuilder.equal(root, filterDTO.getReplies()));
                    }

                    if (filterDTO.getDocumentStatus() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("status"), filterDTO.getDocumentStatus()));
                    }
                }

                predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
    }

    @Override
    public Page<Document> getRegistrationNumber(String name, Pageable pageable) {
        return documentRepository.findAll(getSpecification(name), pageable);
    }
    @Override
    public Long countAll(Integer documentTypeId, Integer organizationId){
        return documentRepository.countAllByDocumentTypeIdAndOrganizationId(documentTypeId, organizationId);
    }

    @Override
    public Long countAllByStatus(Integer typeId, DocumentStatus status, Integer organizationId){
        return documentRepository.countAllByDocumentTypeIdAndStatusAndOrganizationId(typeId, status, organizationId);
    }

    @Override
    public Long countAllTodaySDocuments(Integer docTypeId, Integer organizationId){
        return documentRepository.countAllByCreatedAtAfterAndDocumentTypeIdAndOrganizationId(getCastedDate(),docTypeId, organizationId);
    }

    @Override
    public Long countAllWhichHaveAdditionalDocuments(Integer documentTypeId, Integer organizationId){
        return documentRepository.countAllByDocumentTypeIdAndAdditionalDocumentIdNotNullAndOrganizationId(documentTypeId, organizationId);
    }

    @Override
    public Long countAll(Integer documentTypeId, Integer organizationId, Integer departmentId){
        return documentRepository.countAllByDocumentTypeIdAndOrganizationIdAndDepartmentId(documentTypeId, organizationId, departmentId);
    }

    @Override
    public Long countAllByStatus(Integer typeId, DocumentStatus status,Integer organizationId, Integer departmentId){
        return documentRepository.countAllByDocumentTypeIdAndStatusAndOrganizationIdAndDepartmentId(typeId, status, organizationId, departmentId);
    }

    @Override
    public Long countAllTodaySDocuments(Integer docTypeId, Integer organizationId, Integer departmentId){
        return documentRepository.countAllByCreatedAtAfterAndDocumentTypeIdAndOrganizationIdAndDepartmentId(getCastedDate(), docTypeId, organizationId, departmentId);
    }

    @Override
    public  Long countAllWhichHaveAdditionalDocuments(Integer documentTypeId, Integer organizationId, Integer departmentId){
        return documentRepository.countAllByDocumentTypeIdAndAdditionalDocumentIdNotNullAndOrganizationIdAndDepartmentId(documentTypeId, organizationId, departmentId);
    }

    @Override
    public Document updateAllParameters(Document document, Integer docSubId, Integer executeForm, Integer controlForm, Set<File> fileSet, Integer communicationToolId, Integer documentOrganizationId, Date docRegDate, User updateUser) {
        Document document1 = getById(document.getId());
        document1.setJournalId(document.getJournalId());
        document1.setDocumentViewId(document.getDocumentViewId());
        document1.setDocRegNumber(document.getDocRegNumber());
        document1.setDocRegDate(docRegDate);
        document1.setContentId(document.getContentId());
        document1.setContent(document.getContent());
        document1.setAdditionalDocumentId(document.getAdditionalDocumentId());
        document1.setPerformerName(document.getPerformerName());
        document1.setPerformerPhone(document.getPerformerPhone());
        document1.setManagerId(document.getManagerId());
        document1.setControlId(document.getControlId());
        if(executeForm!=null){
            document1.setExecuteForm(ExecuteForm.getExecuteForm(executeForm));
        }
        if(controlForm!=null){
            document1.setControlForm(ControlForm.getControlForm(controlForm));
        }

        document1.setContentFiles(fileSet);
        document1.setInsidePurpose(document.getInsidePurpose());
        document1.setUpdateById(updateUser.getId());
        document1.setUpdateAt(new Date());
        document1 = documentRepository.save(document1);

        DocumentSub documentSub = documentSubService.getById(docSubId);
        if (documentSub!=null){
            documentSub.setCommunicationToolId(communicationToolId);
            documentSub.setOrganizationId(documentOrganizationId);
            documentSubService.update(documentSub,updateUser);
        }
        return document1;
    }

    private static Specification<Document> getSpecification(String registrationNumber) {
        return (Specification<Document>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if(registrationNumber != null){
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("registrationNumber")), "%" + registrationNumber.toLowerCase() + "%"));
            }
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Date getCastedDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        return calendar.getTime();
    }

    @Override
    public HashMap<String, Object> getCountersByType(Integer type) {
        HashMap<String, Object> countersList = new HashMap<>();
        countersList.put("all", documentRepository.countAllDocByType(type));

        List<DocumentStatus> documentStatuses = new LinkedList<>();
        documentStatuses.add(DocumentStatus.Initial);
        documentStatuses.add(DocumentStatus.New);
        countersList.put("new", documentRepository.countAllDocByTypeAndStatusIn(type, documentStatuses));

        documentStatuses.clear();
        documentStatuses.add(DocumentStatus.InProgress);
        countersList.put("inProcess", documentRepository.countAllDocByTypeAndStatusIn(type, documentStatuses));

        documentStatuses.clear();
        documentStatuses.add(DocumentStatus.Completed);
        countersList.put("complete", documentRepository.countAllDocByTypeAndStatusIn(type, documentStatuses));

        Calendar calendar = Calendar.getInstance();
        Date beginDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date endDate = calendar.getTime();
        countersList.put("nearDate", documentRepository.countAllDocByTypeAndDueDateBetween(type, beginDate, endDate));

        countersList.put("expired", documentRepository.countAllExpiredDocsByType(type, beginDate));
        return countersList;
    }

    @Override
    public List<Document> getListByAddDocId(Integer addDocId) {
        return documentRepository.findByAdditionalDocumentIdAndDeletedFalse(addDocId);
    }

    @Override
    public Document getTopParentId(Document document) {
        if (document.getAdditionalDocumentId()==null) return document;

        Document documentParent = getById(document.getAdditionalDocumentId());
        if (documentParent==null) return document;
        if (documentParent.getAdditionalDocumentId()==null){
            return documentParent;
        }
        return getTopParentId(documentParent);
    }

    @Override
    public Page<Document> findAllByDocumentTypeIn(List<Integer> types, Pageable pageable) {
        return documentRepository.findAllByDocumentTypeIdInAndDeletedFalse(types, pageable);
    }
    private static Specification<Document> getSpesificationForReference(
            final Integer organizationId,
            final Integer documentTypeId,

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
        return (Specification<Document>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (organizationId != null) {
                //ushbu tashkilotga tegishli hujjatlar chiqishi uchun, user boshqa organizationga o'tsa eskisi ko'rinmaydi
                predicates.add(criteriaBuilder.equal(root.get("document").get("organizationId"), organizationId));
            }
            if (documentOrganizationId != null) {
                predicates.add(criteriaBuilder.equal(root.join("document").join("documentSubs").get("organizationId"), documentOrganizationId));
            }
            if (documentTypeId != null) {
                //kiruvchi va chiquvchi hujjatlar ajratish uchun
                predicates.add(criteriaBuilder.equal(root.get("document").get("documentTypeId"), documentTypeId));
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
    @Override
    public Page<Document> findFiltered(Integer organizationId, Integer documentTypeId, Integer documentOrganizationId, String docRegNumber, String registrationNumber, Date dateBegin, Date dateEnd, String taskContent, String content, Integer performerId, Integer taskSubType, Integer taskSubStatus, Date deadlineDateBegin, Date deadlineDateEnd, Integer type, Set<Integer> status, Integer departmentId, Integer receiverId, Boolean specialControll, Pageable pageable) {
        return documentRepository.findAll(getSpesificationForReference(
                organizationId, documentTypeId,
                documentOrganizationId, docRegNumber, registrationNumber, dateBegin, dateEnd, taskContent, content, performerId, taskSubType, taskSubStatus,
                deadlineDateBegin, deadlineDateEnd, type, status, departmentId, receiverId,specialControll), pageable);
    }
}
