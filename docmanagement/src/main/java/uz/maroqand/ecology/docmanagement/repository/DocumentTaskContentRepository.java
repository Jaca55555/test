package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskContent;

import java.util.List;

public interface DocumentTaskContentRepository extends DataTablesRepository<DocumentTaskContent, Integer>, JpaRepository<DocumentTaskContent, Integer> {
    Page<DocumentTaskContent> findAllByContentContainingOrderByIdDesc(String name, Pageable pageable);
    @Query("SELECT d FROM DocumentTaskContent d LEFT JOIN User dt ON d.createdById = dt.id WHERE  dt.organizationId=?1")
    List<DocumentTaskContent> findAllByOrganizationId(Integer organizationId);
}
