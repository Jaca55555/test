package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskContent;
import uz.maroqand.ecology.docmanagement.repository.DocumentTaskContentRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskContentService;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;



@Service
public class DocumentTaskContentServiceImpl implements DocumentTaskContentService {

    private final DocumentTaskContentRepository documentTaskContentRepository;

    @Autowired
    public DocumentTaskContentServiceImpl(DocumentTaskContentRepository documentTaskContentRepository) {
        this.documentTaskContentRepository = documentTaskContentRepository;
    }

    @Override
    public DocumentTaskContent getById(Integer id) {
        Optional<DocumentTaskContent> response = documentTaskContentRepository.findById(id);
        return response.orElse(null);
    }

    @Override
    public List<DocumentTaskContent> getTaskContentList(Integer organizationId) {
        return documentTaskContentRepository.findAllByOrganizationId(organizationId);
    }


    @Override
    public Page<DocumentTaskContent> getTaskContentFilterPage(String content,Integer organizationId ,Pageable pageable) {
        return documentTaskContentRepository.findAll(getFilteringSpecification(content,organizationId), pageable);
    }
    private static Specification<DocumentTaskContent> getFilteringSpecification(String content, Integer organizationId) {
        return (Specification<DocumentTaskContent>) (root, criteriaQuery, criteriaBuilder) -> {
            Join<DocumentTaskContent, User> joinUser = root.join("user");
            List<Predicate> predicates = new LinkedList<>();
            if(organizationId!=null){
                predicates.add(criteriaBuilder.equal(joinUser.get("organizationId"), organizationId));
            }
            if(content != null)
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%" + content.toLowerCase() + "%"));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    @Override
    public DocumentTaskContent save(DocumentTaskContent desc){
        return documentTaskContentRepository.save(desc);
    }

    @Override
    public void delete(DocumentTaskContent taskContent) {
        documentTaskContentRepository.delete(taskContent);
    }
}
