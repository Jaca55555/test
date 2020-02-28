package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;


import org.springframework.data.jpa.repository.Query;
import uz.maroqand.ecology.docmanagement.entity.LibraryCategory;

import java.util.List;

public interface LibraryCategoryService {
    LibraryCategory getById(Integer id) throws IllegalArgumentException;

    LibraryCategory update(LibraryCategory libraryCategory);


    LibraryCategory create(LibraryCategory libraryCategory);

    LibraryCategory updateByIdFromCache(Integer id);
    List<LibraryCategory> findAll();
    DataTablesOutput<LibraryCategory> getAll(DataTablesInput input);
   Page<LibraryCategory> getFiltered(String name,String parent_name, Pageable pageable);


}

