package uz.maroqand.ecology.docmanagement.service.interfaces;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import uz.maroqand.ecology.docmanagement.entity.Library;
import uz.maroqand.ecology.docmanagement.entity.LibraryCategory;


import java.util.Date;



public interface LibraryService {
    Library getById(Integer id) throws IllegalArgumentException;
    Library update(Library library);
    Library create(Library library, LibraryCategory libraryCategory);
    Library updateByIdFromCache(Integer id);
    DataTablesOutput<Library> getAll(DataTablesInput input);
    Page<Library> getFiltered(String name,Integer categoryId, Pageable pageable);
    Page<Library> getFilter(String name,String ftext, String number, Date dateBegin, Date dateEnd,Integer categoryId,Pageable pageable);
    LibraryCategory getByCategoryId(Integer Id) throws IllegalArgumentException;
}
