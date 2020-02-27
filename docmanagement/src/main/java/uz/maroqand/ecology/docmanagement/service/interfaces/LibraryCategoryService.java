package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import uz.maroqand.ecology.docmanagement.entity.DocumentType;
import uz.maroqand.ecology.docmanagement.entity.LibraryCategory;

import java.util.List;

public interface LibraryCategoryService {
    LibraryCategory getById(Integer id) throws IllegalArgumentException;

    LibraryCategory update(LibraryCategory libraryCategory);


    LibraryCategory create(LibraryCategory libraryCategory);
   // List<LibraryCategory> updateStatusActive();
    LibraryCategory updateByIdFromCache(Integer id);

    DataTablesOutput<LibraryCategory> getAll(DataTablesInput input);
   Page<LibraryCategory> getFiltered(String name, Pageable pageable);
}

