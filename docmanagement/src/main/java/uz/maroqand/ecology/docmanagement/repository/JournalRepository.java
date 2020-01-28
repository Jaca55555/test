package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagement.entity.Journal;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 02.05.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface JournalRepository extends DataTablesRepository<Journal, Integer>, JpaRepository<Journal, Integer> {

    List<Journal> findByStatusTrue();

}
