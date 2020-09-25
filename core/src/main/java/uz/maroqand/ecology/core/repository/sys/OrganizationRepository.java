package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.sys.Organization;
import java.util.List;
/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
    Organization getByName(String name);

    Organization findTop1ByRegionId(Integer regionId);

    Page<Organization> findAll(Specification<Organization> filteringSpecification, Pageable pageable);
}
