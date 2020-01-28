package uz.maroqand.ecology.docmanagment.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import uz.maroqand.ecology.docmanagment.constant.DocumentTypeEnum;
import uz.maroqand.ecology.docmanagment.entity.DocumentType;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 01.04.2019.
 * (uz)
 * (ru)
 */
public interface DocumentTypeService {

    DocumentType getById(Integer id) throws IllegalArgumentException;

    DocumentType updateByIdFromCache(Integer id);

    List<DocumentType> getStatusActive();

    List<DocumentType> removeStatusActive();

    Page<DocumentType> getFiltered(DocumentTypeEnum type, String name, Boolean status, Pageable pageable);

    DataTablesOutput<DocumentType> getAll(DataTablesInput input);

    DocumentType create(DocumentType documentType);

    DocumentType update(DocumentType documentType);
}
