package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import uz.maroqand.ecology.docmanagement.entity.CommunicationTool;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 16.04.2019.
 * (uz)
 */
public interface CommunicationToolService {

    CommunicationTool getById(Integer id);

    CommunicationTool updateCacheableById(Integer id);

    List<CommunicationTool> getStatusActive();

    List<CommunicationTool> updateStatusActive();

    Page<CommunicationTool> findFiltered(Integer id, String service, Pageable pageAble);

    CommunicationTool create(CommunicationTool communicationTool);

    CommunicationTool update(CommunicationTool communicationTool);

    DataTablesOutput<CommunicationTool> getAll(DataTablesInput input);

}
