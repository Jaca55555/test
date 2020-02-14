package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 14.02.2020.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentTaskSubRepository extends JpaRepository<DocumentTaskSub, Integer> {

    List<DocumentTaskSub> findByDocumentIdAndDeletedFalse(Integer documentId);

    List<DocumentTaskSub> findByTaskIdAndDeletedFalse(Integer documentId);

}