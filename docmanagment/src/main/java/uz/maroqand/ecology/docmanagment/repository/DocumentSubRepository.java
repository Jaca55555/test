package uz.maroqand.ecology.docmanagment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagment.entity.DocumentSub;

/**
 * Created by Utkirbek Boltaev on 13.12.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentSubRepository extends JpaRepository<DocumentSub, Integer> {


}
