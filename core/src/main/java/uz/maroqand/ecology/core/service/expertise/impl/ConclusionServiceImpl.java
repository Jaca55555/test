package uz.maroqand.ecology.core.service.expertise.impl;

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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
        if(conclusion.getNumber()!=null){
            return null;
        }

        DocumentRepo documentRepo = documentRepoService.create(DocumentRepoType.Conclusion, conclusion.getId());
        conclusion.setDocumentRepoId(documentRepo.getId());
        conclusion.setStatus(ConclusionStatus.Active);
//        conclusion.setNumber();
//        conclusion.setDate();
//        conclusion.setDeadlineDate();
//        conclusion.setFiles();

        return conclusionRepository.save(conclusion);
    }


    @Override
    public Page<Conclusion> findFiltered(Integer id, Date dateBegin, Date dateEnd, Pageable pageable) {
        return conclusionRepository.findAll(getFilteringSpecification(id,dateBegin,dateEnd),pageable);
    }

    private static Specification<Conclusion> getFilteringSpecification(
            final Integer id,
            final Date dateBegin,
            final Date dateEnd
    ) {
        return new Specification<Conclusion>() {
            @Override
            public Predicate toPredicate(Root<Conclusion> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if(id != null){
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
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
                // Show only registered and non-deleted
                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add( notDeleted );

                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
}
