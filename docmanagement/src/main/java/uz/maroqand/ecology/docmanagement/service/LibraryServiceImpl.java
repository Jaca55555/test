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


import uz.maroqand.ecology.docmanagement.entity.LibraryCategory;
import uz.maroqand.ecology.docmanagement.repository.LibraryCategoryRepository;
import uz.maroqand.ecology.docmanagement.repository.LibraryRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.LibraryService;

import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
@Service
public class LibraryServiceImpl implements LibraryService {


    private final LibraryRepository libraryRepository;
    private final LibraryCategoryRepository libraryCategoryRepository;

    @Autowired
    public LibraryServiceImpl(LibraryRepository libraryRepository,LibraryCategoryRepository libraryCategoryRepository) {
        this.libraryRepository = libraryRepository;
        this.libraryCategoryRepository = libraryCategoryRepository;
    }

    @Override
    public LibraryCategory getByCategoryId(Integer CategoryId) throws IllegalArgumentException {
        if(CategoryId==null) return null;
        return libraryCategoryRepository.getOne(CategoryId);
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
    private static Specification<Library> getFilterSpecification(String name,String ftext, String number, Date dateBegin, Date dateEnd,Integer categoryId) {
        return (Specification<Library>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"));
            }
            if (ftext != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("ftext")),
                        "%" + ftext.toLowerCase() + "%"));
            }
            if (number != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("number")),
                        "%" + number.toLowerCase() + "%"));
            }
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), categoryId));
            }
            if(dateBegin != null && dateEnd != null){
                predicates.add(criteriaBuilder.between(root.get("ldate"), dateBegin ,dateEnd));
            }
            if(dateBegin != null && dateEnd == null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ldate"), dateBegin));
            }

            if(dateBegin == null && dateEnd != null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("ldate"), dateEnd));
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
    public Library create(Library library,LibraryCategory libraryCategory) {
        library.setDeleted(Boolean.FALSE);
        library.setCreatedAt(new Date());
        LibraryCategory category=getByCategoryId(library.getCategoryId());
        if(libraryCategory.getCount()!=null){
        libraryCategory.setCount(category.getCount()+1);}
        else {libraryCategory.setCount(1);}
        return libraryRepository.save(library);
    }
    @Override
    @CachePut(value = "libraryGetById", key = "#id")
    public Library updateByIdFromCache(Integer id) {
        if(id==null)return null;
        return libraryRepository.getOne(id);
    }

    @Override
    public Integer countByCategoryId(Integer categoryId) {
        return  libraryRepository.countByCategoryId(categoryId);
    }

    @Override
    public DataTablesOutput<Library> getAll(DataTablesInput input) {
        return null;
    }

    @Override
    public Page<Library> getFiltered(String name,Integer categoryId,Pageable pageable) {
        return libraryRepository.findAll(getFilteringSpecification(name,categoryId), pageable);
    }
    @Override
    public Page<Library> getFilter(String name,String ftext, String number, Date dateBegin, Date dateEnd,Integer categoryId,Pageable pageable) {
        return libraryRepository.findAll(getFilterSpecification(name,ftext,number,dateBegin,dateEnd,categoryId), pageable);
    }




}
