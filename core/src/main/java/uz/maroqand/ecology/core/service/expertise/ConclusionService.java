package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;

import java.util.Date;
import java.util.List;

public interface ConclusionService {

    Conclusion getById(Integer id);

    Conclusion getByRegApplicationIdLast(Integer id);

    Conclusion getByIdAndRegApplicationId(Integer id, Integer regApplicationId);

    Page<Conclusion> findFiltered(Integer id, Date dateBegin, Date dateEnd, Pageable pageable);

    Conclusion save(Conclusion conclusion);

}
