package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.Translation;
import uz.maroqand.ecology.core.repository.sys.TranslationRepository;
import uz.maroqand.ecology.core.service.sys.TranslationService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
@Service
public class TranslationServiceImpl implements TranslationService {

    private final TranslationRepository translationRepository;

    @Autowired
    public TranslationServiceImpl(TranslationRepository translationRepository) {
        this.translationRepository = translationRepository;
    }

    @Cacheable(value = "translationFindByName", key = "#name",unless="#result == ''")
    public Translation findByName(String name) {
        return translationRepository.findByName(name);
    }

    @CachePut(value = "translationFindByName", key = "#name")
    public Translation updateByName(String name) {
        return translationRepository.findByName(name);
    }

    public Translation getById(Integer id) {
        return translationRepository.findById(id).get();
    }

    public Translation create(Translation translation) {
        translationRepository.save(translation);
        return translation;
    }

    public Translation update(Translation translation) {
        translationRepository.save(translation);
        return translation;
    }

    @Override
    public Page<Translation> findFiltered(
            String translationTag,
            String translationRu,
            String translationUz,
            String translationEn,
            String translationOz,
            Pageable pageable
    ) {
        return translationRepository.findAll(getFilteringSpecification(translationTag, translationRu, translationUz,translationEn,translationOz),pageable);
    }

    private static Specification<Translation> getFilteringSpecification(
            final String translationTag,
            final String translationRu,
            final String translationUz,
            final String translationEn,
            final String translationOz
    ) {
        return new Specification<Translation>() {
            @Override
            public Predicate toPredicate(Root<Translation> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                if (translationTag != null) {
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + translationTag + "%"));
                }
                if (translationRu != null) {
                    predicates.add(criteriaBuilder.like(root.get("russian"), "%" + translationRu + "%"));
                }
                if (translationUz != null) {
                    predicates.add(criteriaBuilder.like(root.get("uzbek"), "%" + translationUz + "%"));
                }
                if (translationEn != null) {
                    predicates.add(criteriaBuilder.like(root.get("english"), "%" + translationEn + "%"));
                }
                if (translationOz != null) {
                    predicates.add(criteriaBuilder.like(root.get("ozbek"), "%" + translationOz + "%"));
                }

                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }

}
