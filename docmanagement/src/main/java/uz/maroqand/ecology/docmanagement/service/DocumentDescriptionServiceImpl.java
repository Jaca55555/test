package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.docmanagement.dto.JournalFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;
import uz.maroqand.ecology.docmanagement.entity.Journal;
import uz.maroqand.ecology.docmanagement.repository.DocumentDescriptionRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentDescriptionService;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Utkirbek Boltaev on 13.02.2020.
 * (uz)
 */

@Service
public class DocumentDescriptionServiceImpl implements DocumentDescriptionService {

    private final DocumentDescriptionRepository documentDescriptionRepository;

    @Autowired
    public DocumentDescriptionServiceImpl(DocumentDescriptionRepository documentDescriptionRepository) {
        this.documentDescriptionRepository = documentDescriptionRepository;
    }

    @Override
    public DocumentDescription getById(Integer id) {
        Optional<DocumentDescription> response = documentDescriptionRepository.findById(id);
        return response.orElse(null);
    }

    @Override
    public List<DocumentDescription> getDescriptionList() {
        return documentDescriptionRepository.findAll();
    }

    @Override
    public List<DocumentDescription> findAllByOrganizationId(Integer organizationId) {
        return documentDescriptionRepository.findAllByOrganizationId(organizationId);
    }

    @Override
    public Page<DocumentDescription> getDescriptionFilterPage(String content,Integer organizationId ,Pageable pageable) {
        return documentDescriptionRepository.findAll(getFilteringSpecification(content,organizationId), pageable);
    }
    private static Specification<DocumentDescription> getFilteringSpecification(String content,Integer organizationId) {
        return (Specification<DocumentDescription>) (root, criteriaQuery, criteriaBuilder) -> {
            Join<DocumentDescription, User> joinUser = root.join("user");
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
    public DocumentDescription save(DocumentDescription desc){
        return documentDescriptionRepository.save(desc);
    }

    @Override
    public void delete(DocumentDescription description) {
        documentDescriptionRepository.delete(description);
    }
}
