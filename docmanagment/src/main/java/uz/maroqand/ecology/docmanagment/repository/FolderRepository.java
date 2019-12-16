package uz.maroqand.ecology.docmanagment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagment.entity.Folder;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer>,JpaSpecificationExecutor<Folder> {

    List<Folder> findByDeletedFalse();

}
