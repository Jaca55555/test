package uz.maroqand.ecology.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.dto.excel.DocumentOrderStatus;
import uz.maroqand.ecology.core.entity.DocumentOrder;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 18.06.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentOrderRepository extends JpaRepository<DocumentOrder, Integer> {

    @Query("SELECT do.id FROM DocumentOrder do WHERE do.deleted=FALSE AND do.status IN (:statuses) ORDER BY do.id ASC")
    List<Integer> findIdsByStatusInAndDeletedFalse(@Param("statuses") List<DocumentOrderStatus> status);

    Page<DocumentOrder> findByDeletedFalseAndOrderedByIdOrderByIdDesc(Integer orderedById, Pageable pageable);

    DocumentOrder findByIdAndDeletedFalseAndOrderedById(Integer documentOrderId, Integer orderedById);

    DocumentOrder findByIdAndDeletedFalse(Integer documentOrderId);

    Page<DocumentOrder> findByDeletedFalseOrderByIdDesc(Pageable pageable);


}
