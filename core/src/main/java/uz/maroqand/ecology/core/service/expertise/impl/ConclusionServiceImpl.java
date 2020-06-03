package uz.maroqand.ecology.core.service.expertise.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.ConclusionStatus;
import uz.maroqand.ecology.core.constant.sys.DocumentRepoType;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.sys.DocumentRepo;
import uz.maroqand.ecology.core.repository.expertise.ConclusionRepository;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.sys.DocumentRepoService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service
public class ConclusionServiceImpl implements ConclusionService {

    private final ConclusionRepository conclusionRepository;
    private final DocumentRepoService documentRepoService;

    @Autowired
    public ConclusionServiceImpl(ConclusionRepository conclusionRepository, DocumentRepoService documentRepoService) {
        this.conclusionRepository = conclusionRepository;
        this.documentRepoService = documentRepoService;
    }

    @Override
    public Conclusion getById(Integer id) {
        return conclusionRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public Conclusion save(Conclusion conclusion) {
        return conclusionRepository.save(conclusion);
    }

    @Override
    public Conclusion getByRegApplicationIdLast(Integer id) {
        return conclusionRepository.findTop1ByRegApplicationIdAndDeletedFalseOrderByIdDesc(id);
    }

    @Override
    public Conclusion create(Integer regApplicationId, String text, Integer userId) {
        Conclusion conclusion = new Conclusion();
        conclusion.setRegApplicationId(regApplicationId);
        conclusion.setHtmlText(text);

        conclusion.setStatus(ConclusionStatus.Initial);
        conclusion.setCreatedAt(new Date());
        conclusion.setCreatedById(userId);
        return conclusionRepository.save(conclusion);
    }

    @Override
    public Conclusion update(Conclusion conclusion, String text, Integer userId) {
        conclusion.setHtmlText(text);

        conclusion.setUpdateAt(new Date());
        conclusion.setUpdateById(userId);
        return conclusionRepository.save(conclusion);
    }

    @Override
    public Conclusion complete(Integer conclusionId){
        Conclusion conclusion = getById(conclusionId);
        /*if(conclusion.getNumber()!=null){
            return null;
        }*/

        DocumentRepo documentRepo = documentRepoService.create(DocumentRepoType.Conclusion, conclusion.getId());
        conclusion.setDocumentRepoId(documentRepo.getId());
        conclusion.setStatus(ConclusionStatus.Active);
//        conclusion.setNumber(conclusion.getId().toString()); //todo xulosa raqami ketma ket ketadimi prefix suffix bo’lmaydimi?
        conclusion.setDate(new Date());
//        conclusion.setDeadlineDate();
//        conclusion.setFiles();

        return conclusionRepository.save(conclusion);
    }


    @Override
    public Page<Conclusion> findFiltered(Integer id, Date dateBegin, Date dateEnd, Integer tin, String name,Pageable pageable) {
        Set<ConclusionStatus> conclusionStatusIds  = new HashSet<>();
        conclusionStatusIds.add(ConclusionStatus.Active);
        conclusionStatusIds.add(ConclusionStatus.Expired);
        return conclusionRepository.findAll(getFilteringSpecification(id,dateBegin,dateEnd,tin,conclusionStatusIds,name),pageable);
    }

    private static Specification<Conclusion> getFilteringSpecification(
            final Integer id,
            final Date dateBegin,
            final Date dateEnd,
            final Integer tin,
            final Set<ConclusionStatus> conclusionStatuses,
            final String name
    ) {
        return new Specification<Conclusion>() {
            @Override
            public Predicate toPredicate(Root<Conclusion> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if(id != null){
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }

                if(conclusionStatuses != null){
                    predicates.add(criteriaBuilder.in(root.get("status")).value(conclusionStatuses));
                }

                if(dateBegin != null && dateEnd != null){
                    predicates.add(criteriaBuilder.between(root.get("date"), dateBegin, dateEnd));
                }

                if(dateBegin != null && dateEnd == null){
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), dateBegin));
                }

                if(dateBegin == null && dateEnd != null){
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), dateEnd));
                }

                if (tin!=null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("applicant").<String>get("tin"), tin));

                }

                if (name != null) {
                    predicates.add(criteriaBuilder.like(root.join("regApplication").get("applicant").<String>get("name"), "%" + name + "%"));
                }

                // Show only registered and non-deleted
                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );

                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
}
