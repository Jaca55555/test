package uz.maroqand.ecology.docmanagment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagment.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagment.entity.Document;
import uz.maroqand.ecology.docmanagment.repository.DocumentRepository;
import uz.maroqand.ecology.docmanagment.service.interfaces.DocumentService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public Page<Document> findFiltered(
            DocFilterDTO filterDTO,
            Pageable pageable
    ) {
        return documentRepository.findAll(getSpesification(filterDTO), pageable);
    }

    private static Specification<Document> getSpesification(DocFilterDTO filterDTO) {
        return new Specification<Document>() {
            @Override
            public Predicate toPredicate(Root<Document> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if (filterDTO != null) {
                    if (filterDTO.getDocumentId() != null) {
                        predicates.add(criteriaBuilder.equal(root.get("id"), filterDTO.getDocumentId()));
                    }
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }
}
