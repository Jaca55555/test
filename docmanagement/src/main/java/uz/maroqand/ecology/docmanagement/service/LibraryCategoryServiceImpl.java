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
import uz.maroqand.ecology.docmanagement.entity.DocumentType;
import uz.maroqand.ecology.docmanagement.entity.LibraryCategory;
import uz.maroqand.ecology.docmanagement.repository.DocumentTypeRepository;
import uz.maroqand.ecology.docmanagement.repository.LibraryCategoryRepository;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTypeService;
import uz.maroqand.ecology.docmanagement.service.interfaces.LibraryCategoryService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
@Service
public class LibraryCategoryServiceImpl implements LibraryCategoryService {

    private final LibraryCategoryRepository libraryCategoryRepository;

    @Autowired
    public LibraryCategoryServiceImpl(LibraryCategoryRepository libraryCategoryRepository) {
        this.libraryCategoryRepository = libraryCategoryRepository;
    }

    //GetById
    @Override
    @Cacheable(value = "libraryCategoryGetById", key = "#id", condition="#id != null", unless="#result == null")
    public LibraryCategory getById(Integer id) throws IllegalArgumentException {
        if(id==null) return null;
        return libraryCategoryRepository.getOne(id);
    }

    //PutById
    @Override
    @CachePut(value = "libraryCategoryGetById", key = "#id")
    public LibraryCategory updateByIdFromCache(Integer id) {
        if(id==null)return null;
        return libraryCategoryRepository.getOne(id);
    }


    @Override
    public Page<LibraryCategory> getFiltered(String name,String parent_name,Pageable pageable) {
        return libraryCategoryRepository.findAll(getFilteringSpecification(name,parent_name), pageable);
    }
    @Override
    public List<LibraryCategory> findAll(){
        return libraryCategoryRepository.findAll();
    }
    @Override
    public DataTablesOutput<LibraryCategory> getAll(DataTablesInput input) {
        return libraryCategoryRepository.findAll(input);
    }

    private static Specification<LibraryCategory> getFilteringSpecification(String name,String parent_name) {
        return (Specification<LibraryCategory>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"));
            }


            predicates.add( criteriaBuilder.equal(root.get("deleted"), false) );
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    int i=0;
    @Override
    public LibraryCategory create(LibraryCategory libraryCategory) {
        libraryCategory.setDeleted(Boolean.FALSE);
        libraryCategory.setCreatedAt(new Date());
       if(libraryCategory.getParent()==null) {
           libraryCategory.setLevel(0);
       }
       if(libraryCategory.getParent()!=null){
           LibraryCategory parent = getById(libraryCategory.getParent());
           libraryCategory.setLevel(parent.getLevel()+1);
       }

        return libraryCategoryRepository.save(libraryCategory);
    }

    @Override
    public LibraryCategory update(LibraryCategory libraryCategory) {
        return libraryCategoryRepository.save(libraryCategory);
    }




}
