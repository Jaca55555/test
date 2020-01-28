package uz.maroqand.ecology.docmanagment.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import uz.maroqand.ecology.docmanagment.entity.DocumentOrganization;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz)
 * (ru)
 */
public interface DocumentOrganizationService {

    DocumentOrganization getById(Integer id);

    DocumentOrganization updateByIdFromCache(Integer id);

    List<DocumentOrganization> getStatusActive();

    List<DocumentOrganization> organizationGetStatusActive();

    DataTablesOutput<DocumentOrganization> getAll(DataTablesInput input);

    Page<DocumentOrganization> findFiltered(Integer id, String name, Integer status, Pageable pageable);

    Page<DocumentOrganization> getOrganizationList(String name, Pageable pageable);

    DocumentOrganization create(DocumentOrganization communicationTool);

    DocumentOrganization update(DocumentOrganization communicationTool);

}
