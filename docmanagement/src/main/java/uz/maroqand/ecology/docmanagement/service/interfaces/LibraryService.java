package uz.maroqand.ecology.docmanagement.service.interfaces;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import uz.maroqand.ecology.docmanagement.entity.Library;


public interface LibraryService {
    Library getById(Integer id) throws IllegalArgumentException;
    Library update(Library library);
    Library create(Library library);
    Library updateByIdFromCache(Integer id);
    DataTablesOutput<Library> getAll(DataTablesInput input);
    Page<Library> getFiltered(String name,Integer categoryId, Pageable pageable);


}
