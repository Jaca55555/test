package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.sys.File;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Repository
public interface FileRepository extends JpaRepository<File, Integer> {

    File findByIdAndDeletedFalse(Integer fileId);

    File findByIdAndUploadedByIdAndDeletedFalse(Integer fileId, Integer userId);

    File findByNameAndDeletedFalse(String fileName);
}
