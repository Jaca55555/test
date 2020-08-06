package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;

import java.util.List;


/**
 * Created by Utkirbek Boltaev on 09.07.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface ConclusionRepository extends JpaRepository<Conclusion, Integer>, JpaSpecificationExecutor<Conclusion> {

    Conclusion findByIdAndDeletedFalse(Integer id);

    List<Conclusion> findByRegApplicationIdAndDeletedFalseOrderByIdDesc(Integer id);

    Conclusion findByIdAndRegApplicationIdAndDeletedFalse(Integer id,Integer regApplicationId);

    Conclusion findTop1ByRegApplicationIdAndDeletedFalseOrderByIdDesc(Integer id);

}