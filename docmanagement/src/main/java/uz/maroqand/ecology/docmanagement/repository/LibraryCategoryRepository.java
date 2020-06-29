package uz.maroqand.ecology.docmanagement.repository;


import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import uz.maroqand.ecology.docmanagement.entity.LibraryCategory;

import java.util.List;

public interface LibraryCategoryRepository extends DataTablesRepository<LibraryCategory, Integer>, JpaRepository<LibraryCategory, Integer> {


  List<LibraryCategory> findAllByDeletedFalse();

}
