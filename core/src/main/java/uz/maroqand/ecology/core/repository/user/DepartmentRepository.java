package uz.maroqand.ecology.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.user.Department;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 28.03.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer>, JpaSpecificationExecutor<Department> {

    Department findByIdAndDeletedFalse(Integer id);

    List<Department> findByOrganizationIdAndDeletedFalse(Integer id);

    List<Department> findAllByDeletedFalseOrderByIdAsc();
    List<Department> findAllByDeletedFalseAndOrganizationId(Integer organizationId);
    List<Department> findByParentIdAndDeletedFalse(Integer parentId);

    List<Department> findByParentIdIsNull();

}
