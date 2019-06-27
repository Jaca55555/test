package uz.maroqand.ecology.core.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.Department;
import uz.maroqand.ecology.core.repository.user.DepartmentRepository;
import uz.maroqand.ecology.core.service.user.DepartmentService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 28.03.2019.
 * (uz)
 * (ru)
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Cacheable(value = "departmentGetById", key = "#id",unless="#result == ''")
    public Department getById(Integer id) {
        return departmentRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public List<Department> getListByOrganizationId(Integer id) {
        return departmentRepository.findByOrganizationIdAndDeletedFalse(id);
    }

    //getAll
    @Override
    @Cacheable("departmentGetAll")
    public List<Department> getAll() {
        return departmentRepository.findAll();
    }

    //RemoveAllFromCache
    @Override
    @CacheEvict(value = "departmentGetAll", allEntries = true)
    public List<Department> removeStatusActive() {
        return departmentRepository.findAll();
    }

    @Override
    public Page<Department> findFiltered(
            Integer id,
            String name,
            Pageable pageable) {
        return departmentRepository.findAll(getFilteringSpecification(id,name), pageable);
    }

    @Override
    public Department save(Department department) {
        return  departmentRepository.save(department);
    }

    @Override
    public List<Department> findList() {
        return departmentRepository.findAllByDeletedFalseOrderByIdAsc();
    }

    @Override
    public List<Department> findListChild() {
        return departmentRepository.findByParentIdIsNull();
    }

    @Override
    public List<Department> findByParentId(Integer parentId) {
        List<Department> departmentList = departmentRepository.findAllByDeletedFalseOrderByIdAsc();
        List<Department> departmentList1 = new LinkedList<>();
        for (Department department :departmentList){
            if (department.getParentId()==parentId){
                departmentList1.add(department);
            }
        }
        return departmentList1;
    }

    private static Specification<Department> getFilteringSpecification(
            final Integer id,
            final String name
        ) {
        return new Specification<Department>() {
            @Override
            public Predicate toPredicate(Root<Department> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<>();

                System.out.println("name="+name);
                System.out.println("id="+id);
                if (id != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                if (name != null) {
                    predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
                }
                // Show only registered and non-deleted
                Predicate notDeleted = criteriaBuilder.equal(root.get("deleted"), false);
                predicates.add(notDeleted);

                Predicate overAll = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                return overAll;
            }
        };
    }
}