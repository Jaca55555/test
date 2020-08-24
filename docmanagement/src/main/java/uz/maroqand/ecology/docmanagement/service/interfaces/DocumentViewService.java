package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.domain.Page;
import uz.maroqand.ecology.docmanagement.entity.DocumentView;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.05.2019.
 * (uz)
 * (ru)
 */
public interface DocumentViewService {

    DocumentView getById(Integer id);

    DocumentView updateByIdFromCache(Integer id);

    List<DocumentView> getStatusActive();
    List<DocumentView> getStatusActiveAndByType(Integer organizationId,String type);
    List<DocumentView> documentViewGetStatusActive();

    Page<DocumentView> findFiltered(String name,Integer organizationId, Integer status, String locale, Pageable pageable);

    DocumentView create(DocumentView communicationTool);

    DocumentView update(DocumentView communicationTool);

    DataTablesOutput<DocumentView> getAll(DataTablesInput input);

}
