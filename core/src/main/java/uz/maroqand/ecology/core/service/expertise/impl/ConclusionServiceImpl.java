package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.expertise.ConclusionStatus;
import uz.maroqand.ecology.core.constant.sys.DocumentRepoType;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;
import uz.maroqand.ecology.core.entity.sys.DocumentRepo;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.repository.expertise.ConclusionRepository;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.sys.DocumentEditorService;
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
    public Conclusion getByRepoId(Integer id) {
        return conclusionRepository.findByDocumentRepoIdAndDeletedFalse(id);
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

        DocumentRepo documentRepo = documentRepoService.create(DocumentRepoType.Conclusion, conclusion.getId());
        conclusion.setDocumentRepoId(documentRepo.getId());
        conclusion.setStatus(ConclusionStatus.Active);
        return conclusionRepository.save(conclusion);
    }

    @Override
    public List<Conclusion> getByRegApplicationId(Integer regApplicationId) {
        return conclusionRepository.findByRegApplicationIdAndDeletedFalseOrderByIdDesc(regApplicationId);
    }


    @Override
    public Page<Conclusion> findFiltered(Integer reviewId, Integer id, Date dateBegin, Date dateEnd, Integer tin,Integer regionId,Integer subRegionId, String name, Category category,Integer regApplicationId, Pageable pageable) {
        Set<ConclusionStatus> conclusionStatusIds  = new HashSet<>();
        conclusionStatusIds.add(ConclusionStatus.Active);
        conclusionStatusIds.add(ConclusionStatus.Expired);
        return conclusionRepository.findAll(getFilteringSpecification(reviewId,id,dateBegin,dateEnd,tin,regionId,subRegionId,conclusionStatusIds,name,category,regApplicationId),pageable);
    }

    private static Specification<Conclusion> getFilteringSpecification(
            final Integer reviewId,
            final Integer id,
            final Date dateBegin,
            final Date dateEnd,
            final Integer tin,
            final Integer regionId,
            final Integer subRegionId,
            final Set<ConclusionStatus> conclusionStatuses,
            final String name,
            final Category category,
            final Integer regApplicationId
    ) {
        return new Specification<Conclusion>() {
            @Override
            public Predicate toPredicate(Root<Conclusion> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if (reviewId!=null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("reviewId"), reviewId));
                }
                if (regApplicationId!=null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("id"), regApplicationId));
                }

                if(id != null){
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                if(regionId!=null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("applicant").get("regionId"),regionId));
                }
                if(subRegionId!=null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("applicant").get("subRegionId"),subRegionId));
                }
                if(conclusionStatuses != null){
                    predicates.add(criteriaBuilder.in(root.get("status")).value(conclusionStatuses));
                }
                if(category !=null){
                    predicates.add(criteriaBuilder.equal(root.join("regApplication").get("category"),category));
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
