package uz.maroqand.ecology.docmanagement.repository;


import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.maroqand.ecology.docmanagement.entity.Library;



public interface LibraryRepository extends DataTablesRepository<Library, Integer>, JpaRepository<Library, Integer>, JpaSpecificationExecutor<Library> {



}

