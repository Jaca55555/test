package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.sys.Soato;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 11.06.2019.
 * (uz)
 */
@Repository
public interface SoatoRepository extends JpaRepository<Soato, Integer> {

    List<Soato> findByLevelOrderByNameAsc(Integer level);

    @Query("SELECT d FROM Soato d WHERE d.parentId=null")
    List<Soato> getAll();
    @Query("SELECT f FROM Soato f WHERE f.id=?1")
    List<Soato> findByParentId(Long id);

    Page<Soato> findAll(Specification<Soato> filteringSpecification, Pageable pageable);
}
