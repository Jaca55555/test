package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import uz.maroqand.ecology.docmanagement.entity.DocumentOrganization;

import java.util.List;
import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz)
 * (ru)
 */
public interface DocumentOrganizationService {

    DocumentOrganization getById(Integer id);
    Set<DocumentOrganization> getByParent(Integer parentId);
    Set<Integer> getByOrganizationId(Integer organizationId);
    DocumentOrganization getByName(String name);

    List<String> getDocumentOrganizationNames();

    DocumentOrganization updateByIdFromCache(Integer id);

    DocumentOrganization updateByNameFromCache(String name);

    List<DocumentOrganization> getStatusActive();

    List<DocumentOrganization> organizationGetStatusActive();

    DataTablesOutput<DocumentOrganization> getAll(DataTablesInput input);

    List<DocumentOrganization> getList();
    List<DocumentOrganization> getLevel(Integer Id);
    Page<DocumentOrganization> findFiltered(Integer id,Integer organizationId, String name, Integer status, Pageable pageable);

    Page<DocumentOrganization> getOrganizationList(String name,Integer organizationId, Pageable pageable);

    DocumentOrganization create(DocumentOrganization documentOrganization);

    DocumentOrganization update(DocumentOrganization documentOrganization);


}
