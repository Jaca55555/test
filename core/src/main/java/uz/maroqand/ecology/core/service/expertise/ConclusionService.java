package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;

import java.util.Date;

public interface ConclusionService {

    Conclusion getById(Integer id);

    Conclusion getByRegApplicationIdLast(Integer id);

    Page<Conclusion> findFiltered(Integer id, Date dateBegin, Date dateEnd, Integer tin, String name,Pageable pageable);

    Conclusion create(Integer regApplicationId, String text, Integer createdById);

    Conclusion update(Conclusion conclusion, String text, Integer createdById);

    Conclusion complete(Integer conclusionId);

}
