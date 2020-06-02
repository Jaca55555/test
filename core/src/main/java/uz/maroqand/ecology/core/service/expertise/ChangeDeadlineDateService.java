package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.constant.expertise.ChangeDeadlineDateStatus;
import uz.maroqand.ecology.core.entity.expertise.Activity;
import uz.maroqand.ecology.core.entity.expertise.ChangeDeadlineDate;

import java.util.List;

public interface ChangeDeadlineDateService {

    ChangeDeadlineDate getById(Integer id);

    Page<ChangeDeadlineDate> findFiltered(Integer regApplicationId, ChangeDeadlineDateStatus changeDeadlineDateStatus, Pageable pageable);

    List<ChangeDeadlineDate> getListByRegApplicationId(Integer id);

    ChangeDeadlineDate save(ChangeDeadlineDate changeDeadlineDate);

    ChangeDeadlineDate getByRegApplicationId(Integer id);

    Integer getCountByStatusInitial();
}
