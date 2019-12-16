package uz.maroqand.ecology.docmanagment.service.interfaces;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import uz.maroqand.ecology.docmanagment.entity.DocumentView;
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

    List<DocumentView> documentViewGetStatusActive();

    DocumentView create(DocumentView communicationTool);

    DocumentView update(DocumentView communicationTool);

    DataTablesOutput<DocumentView> getAll(DataTablesInput input);

}
