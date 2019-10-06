package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.sys.DocumentRepo;

/**
 * Created by Utkirbek Boltaev on 07.10.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface DocumentRepoRepository extends JpaRepository<DocumentRepo, Integer> {

    DocumentRepo findByUuid(String uuid);

}
