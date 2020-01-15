package uz.maroqand.ecology.docmanagment.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.docmanagment.entity.CommunicationTool;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 16.04.2019.
 * (uz)
 * (ru)
 */
@Repository
public interface CommunicationToolRepository extends DataTablesRepository<CommunicationTool, Integer>, JpaRepository<CommunicationTool, Integer>, JpaSpecificationExecutor<CommunicationTool> {

    CommunicationTool findByIdAndDeletedFalse(Integer id);

    List<CommunicationTool> findByStatusTrueOrderByIdAsc();

}
