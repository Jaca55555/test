package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;


import java.util.List;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 03.02.2020
 */
public interface DocumentDescriptionRepository extends DataTablesRepository<DocumentDescription, Integer>, JpaRepository<DocumentDescription, Integer> {
    Page<DocumentDescription> findAllByContentContainingOrderByIdDesc(String name, Pageable pageable);

    @Query("SELECT d FROM DocumentDescription d LEFT JOIN User dt ON d.createdById = dt.id WHERE  dt.organizationId=?1")
    List<DocumentDescription> findAllByOrganizationId(Integer organizationId);

}
