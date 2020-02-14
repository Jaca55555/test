package uz.maroqand.ecology.docmanagement.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.docmanagement.dto.DocFilterDTO;
import uz.maroqand.ecology.docmanagement.entity.DocumentTaskSub;

import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 15.02.2020.
 * (uz)
 * (ru)
 */
public interface DocumentTaskSubService {

    Page<DocumentTaskSub> findFiltered(
            DocFilterDTO filterDTO,
            Date deadlineDateBegin,
            Date deadlineDateEnd,
            Integer type,
            Integer status,
            Integer departmentId,
            Integer receiverId,
            Pageable pageable
    );

}
