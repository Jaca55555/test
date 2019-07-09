package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;

/**
 * Created by Utkirbek Boltaev on 09.07.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface ConclusionRepository extends JpaRepository<Conclusion, Integer> {


}
