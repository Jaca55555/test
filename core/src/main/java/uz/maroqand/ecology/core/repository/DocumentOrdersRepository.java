package uz.maroqand.ecology.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.maroqand.ecology.core.constant.order.DocumentOrderStatus;
import uz.maroqand.ecology.core.entity.DocumentOrder;

import java.util.Date;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 24.07.2018.
 * (uz)
 * (ru)
 */
public interface DocumentOrdersRepository extends JpaRepository<DocumentOrder, Integer> {

    @Query("SELECT do.id FROM DocumentOrder do WHERE do.deleted=FALSE AND do.status IN (:statuses) ORDER BY do.id ASC")
    List<Integer> findIdsByStatusInAndDeletedFalse(@Param("statuses") List<DocumentOrderStatus> status);

    Page<DocumentOrder> findByDeletedFalseAndOrderedByIdOrderByIdDesc(Integer orderedById, Pageable pageable);

    DocumentOrder findByIdAndDeletedFalseAndOrderedById(Integer documentOrderId, Integer orderedById);

    Page<DocumentOrder> findByDeletedFalseOrderByIdDesc(Pageable pageable);

    Page<DocumentOrder> findByDeletedFalseAndRegisteredAtBeforeOrderByIdAsc(Date yesterday, Pageable pageRequest);

    Page<DocumentOrder> findByDeletedFalseOrderByIdDesc(Integer id, Pageable pageable);

    Page<DocumentOrder> findByUserIdAndDeletedFalseOrderByIdDesc(Integer id, Pageable pageable);
}
