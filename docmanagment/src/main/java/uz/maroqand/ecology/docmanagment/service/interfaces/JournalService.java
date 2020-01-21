package uz.maroqand.ecology.docmanagment.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import uz.maroqand.ecology.docmanagment.dto.JournalFilterDTO;
import uz.maroqand.ecology.docmanagment.entity.Journal;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 02.05.2019.
 * (uz)
 * (ru)
 */
public interface JournalService {

    Journal getById(Integer id) throws IllegalArgumentException;

    Journal updateByIdFromCache(Integer id);

    List<Journal> getStatusActive();

    List<Journal> removeStatusActive();

    Page<Journal> getFiltered(JournalFilterDTO filterDTO, Pageable pageable);

    DataTablesOutput<Journal> getAll(DataTablesInput input);

    Journal create(Journal journal);

    Journal update(Journal journal);

}
