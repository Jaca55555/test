package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.entity.DocumentSub;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentSubRepository extends JpaRepository<DocumentSub, Integer> {

    List<DocumentSub> findByDocumentIdAndDeletedFalse(Integer documentId);

    DocumentSub findOneByDocumentId(Integer id);

    DocumentSub findByIdAndDeletedFalse(Integer id);
}