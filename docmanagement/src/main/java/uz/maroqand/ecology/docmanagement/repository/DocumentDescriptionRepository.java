package uz.maroqand.ecology.docmanagement.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.maroqand.ecology.docmanagement.entity.DocumentDescription;

import java.util.List;

/**
 * Created by Namazov Jamshid
 * email: <jamwid07@mail.ru>
 * date: 03.02.2020
 */
public interface DocumentDescriptionRepository extends DataTablesRepository<DocumentDescription, Integer>, JpaRepository<DocumentDescription, Integer>
{
    List<DocumentDescription> findAll();
}
