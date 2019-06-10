package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.constant.sys.TableHistoryEntity;
import uz.maroqand.ecology.core.entity.sys.TableHistory;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Repository
public interface TableHistoryRepository extends JpaRepository<TableHistory, Integer> {

    List<TableHistory> findByEntityAndEntityIdOrderByIdDesc(TableHistoryEntity entity, Integer entityId);

}
