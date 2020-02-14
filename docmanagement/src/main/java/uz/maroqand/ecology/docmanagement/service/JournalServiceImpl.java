package uz.maroqand.ecology.docmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagement.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagement.dto.JournalFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Journal;
import uz.maroqand.ecology.docmanagement.repository.JournalRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.JournalService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 02.05.2019.
 * (uz)
 * (ru)
 */
@Service
public class JournalServiceImpl implements JournalService {

    private final JournalRepository journalRepository;

    @Autowired
    public JournalServiceImpl(JournalRepository journalRepository) {
        this.journalRepository = journalRepository;
    }

    @Override
    @Cacheable(value = "journalGetById", key = "#id", condition="#id != null", unless="#result == null")
    public Journal getById(Integer id) throws IllegalArgumentException {
        if (id==null)return null;
        return updateByIdFromCache(id);
    }

    @Override
    @CachePut(value = "journalGetById", key = "#id")
    public Journal updateByIdFromCache(Integer id) {
        if (id==null)return null;
        return journalRepository.getOne(id);
    }

    @Override
    @Cacheable("journalGetStatusActive")
    public List<Journal> getStatusActive() {
        return setStatusActive();
    }

    @CachePut("journalGetStatusActive")
    public List<Journal> setStatusActive() {
        return journalRepository.findByStatusTrue();
    }


    @Override
    @CacheEvict(value = "journalGetStatusActive", allEntries = true)
    public List<Journal> removeStatusActive() {
        return journalRepository.findAll();
    }

    @Override
    public Page<Journal> getFiltered(JournalFilterDTO filterDTO, Pageable pageable) {
        return journalRepository.findAll(getFilteringSpecification(filterDTO), pageable);
    }

    @Override
    public DataTablesOutput<Journal> getAll(DataTablesInput input) {
        return journalRepository.findAll(input,getFilteringSpecification(new JournalFilterDTO()));
    }

    private static Specification<Journal> getFilteringSpecification(JournalFilterDTO filterDTO) {
        return new Specification<Journal>() {
            @Override
            public Predicate toPredicate(Root<Journal> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();
                if (filterDTO.getDocTypeId() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("documentTypeId"), filterDTO.getDocTypeId()));
                }
                if (filterDTO.getName() != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            "%" + filterDTO.getName().toLowerCase() + "%"
                    ));
                }
                if (filterDTO.getPrefix() != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("prefix")),
                            "%" + filterDTO.getPrefix().toLowerCase() + "%"
                    ));
                }
                if (filterDTO.getSuffix() != null) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("suffix")),
                            "%" + filterDTO.getSuffix().toLowerCase() + "%"
                    ));
                }
                if (filterDTO.getStatus() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), filterDTO.getStatus()));
                }
                predicates.add(criteriaBuilder.equal(root.get("deleted"), false) );
                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

    public Journal create(Journal journal) {
        journal.setDeleted(Boolean.FALSE);
        journal.setCreatedAt(new Date());
        return journalRepository.save(journal);
    }

    public Journal update(Journal journal) {
        return journalRepository.save(journal);
    }

}
