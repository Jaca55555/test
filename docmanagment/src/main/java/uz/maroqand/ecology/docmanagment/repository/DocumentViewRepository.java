package uz.maroqand.ecology.docmanagment.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagment.entity.DocumentView;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.05.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentViewRepository extends DataTablesRepository<DocumentView, Integer>, JpaRepository<DocumentView, Integer> {

    DocumentView findByIdAndDeletedFalse(Integer id);

    List<DocumentView> findByStatusTrue();

}
