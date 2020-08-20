package uz.maroqand.ecology.core.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.user.Department;

import java.util.Date;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 28.03.2019.
 * (uz)
 * (ru)
 */
public interface DepartmentService {

    Department getById(Integer id);

    List<Department> getByOrganizationId(Integer organizationId);

    Page<Department> findFiltered(
            Integer departmentId,
            Integer organizationId,
            Integer parentId,
            String name,
            String nameOz,
            String nameEn,
            String nameRu,
            Pageable pageable
    );
    Page<Department> findFilter(
            Integer departmentId,
            Date dateBegin,
            Date dateEnd,
            String name,
            String nameOz,
            String nameEn,
            String nameRu,
            Pageable pageable
    );

    Department save(Department department);

    List<Department> findByParentId(Integer parentId);
    List<Department> findByOrganizationId(Integer organizationId);

    List<Department> getAll();

}
