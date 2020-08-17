package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import uz.maroqand.ecology.docmanagement.dto.JournalFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.Journal;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 02.05.2019.
 * (uz)
 * (ru)
 */
public interface JournalService {

    Journal getById(Integer id) throws IllegalArgumentException;
    List <Journal> getAll();
    Journal updateByIdFromCache(Integer id);

    List<Journal> getStatusActive(Integer organizationId,Integer documentTypeId);

    List<Journal> updateStatusActive(Integer documentTypeId);

    Page<Journal> getFiltered(JournalFilterDTO filterDTO,Integer organizationId, Pageable pageable);

    DataTablesOutput<Journal> getAll(DataTablesInput input,Integer organizationId);

    Journal create(Journal journal);

    Journal update(Journal journal);

    String getRegistrationNumberByJournalId(Integer journalId);

}