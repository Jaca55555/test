package uz.maroqand.ecology.core.repository.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.client.OKED;

import java.util.List;

@Repository
public interface OKEDRepository extends JpaRepository<OKED, Integer> {

    List<OKED> findByOrderByIdAsc();

    List<OKED> findBySubtypeName(String code);

    List<OKED> findByLevelOrderByIdAsc(Integer level);

    List<OKED> findByIdIn(List<Integer> ids);

  //  OKED findById(Integer id);

    @Query("SELECT id FROM OKED WHERE level = :level AND id IN (:ids)")
    List<Integer> findIdsByLevelAndIdIn(
            @Param("level") Integer level,
            @Param("ids") List<Integer> ids
    );

    List<OKED> findByIdIsGreaterThan(Integer minimumId);
}
