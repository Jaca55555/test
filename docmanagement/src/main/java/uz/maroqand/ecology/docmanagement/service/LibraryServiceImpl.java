package uz.maroqand.ecology.docmanagement.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.docmanagement.entity.Library;


import uz.maroqand.ecology.docmanagement.repository.LibraryRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.LibraryService;

import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
@Service
public class LibraryServiceImpl implements LibraryService {


    private final LibraryRepository libraryRepository;

    @Autowired
    public LibraryServiceImpl(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }
    @Override
    @Cacheable(value = "libraryGetById", key = "#id", condition="#id != null", unless="#result == null")
    public Library getById(Integer id) throws IllegalArgumentException {
        if(id==null) return null;
        return libraryRepository.getOne(id);
    }
    private static Specification<Library> getFilteringSpecification(String name,Integer categoryId) {
        return (Specification<Library>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"));
            }
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), categoryId));
            }


            predicates.add( criteriaBuilder.equal(root.get("deleted"), false) );
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    @Override
    public Library update(Library library) {
        return libraryRepository.save(library);
    }

    @Override
    public Library create(Library library) {
        library.setDeleted(Boolean.FALSE);
        library.setCreatedAt(new Date());
        return libraryRepository.save(library);
    }

    @Override
    @CachePut(value = "libraryGetById", key = "#id")
    public Library updateByIdFromCache(Integer id) {
        if(id==null)return null;
        return libraryRepository.getOne(id);
    }

    @Override
    public DataTablesOutput<Library> getAll(DataTablesInput input) {
        return null;
    }

    @Override
    public Page<Library> getFiltered(String name,Integer categoryId,Pageable pageable) {
        return libraryRepository.findAll(getFilteringSpecification(name,categoryId), pageable);
    }


}
