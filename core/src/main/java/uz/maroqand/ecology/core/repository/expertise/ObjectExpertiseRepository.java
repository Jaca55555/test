package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.ObjectExpertise;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface ObjectExpertiseRepository extends JpaRepository<ObjectExpertise, Integer>, JpaSpecificationExecutor<ObjectExpertise> {

    List<ObjectExpertise> findByDeletedFalse();

}
