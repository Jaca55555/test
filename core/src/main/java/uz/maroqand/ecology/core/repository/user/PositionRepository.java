package uz.maroqand.ecology.core.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.user.Position;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, Integer> {

    Position findByIdAndDeletedFalse(Integer id);

    Position findByIdAndOrganizationIdAndDeletedFalse(Integer id, Integer organizationId);

    List<Position> findAllByDeletedFalseOrderByIdDesc();

    List<Position> findByOrganizationIdAndDeletedFalseOrderByIdDesc(Integer organizationId);

}
