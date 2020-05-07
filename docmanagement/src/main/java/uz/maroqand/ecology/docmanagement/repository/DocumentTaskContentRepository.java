package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskContent;

public interface DocumentTaskContentRepository extends DataTablesRepository<DocumentTaskContent, Integer>, JpaRepository<DocumentTaskContent, Integer> {
    Page<DocumentTaskContent> findAllByContentContainingOrderByIdDesc(String name, Pageable pageable);
}
