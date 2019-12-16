package uz.maroqand.ecology.docmanagment.service.interfaces;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import uz.maroqand.ecology.docmanagment.entity.CommunicationTool;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 16.04.2019.
 * (uz)
 * (ru)
 */
public interface CommunicationToolService {

    CommunicationTool getById(Integer id);

    CommunicationTool updateCacheableById(Integer id);

    List<CommunicationTool> getStatusActive();

    void removeCacheableStatusActive();

    List<CommunicationTool> getList();

    void removeList();

    CommunicationTool create(CommunicationTool communicationTool);

    CommunicationTool update(CommunicationTool communicationTool);

    DataTablesOutput<CommunicationTool> getAll(DataTablesInput input);

}
