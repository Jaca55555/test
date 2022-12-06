package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.entity.expertise.Conclusion;

import java.util.Date;
import java.util.List;

public interface ConclusionService {

    Conclusion getById(Integer id);
    Conclusion getByRepoId(Integer id);

    Conclusion save(Conclusion conclusion);

    Conclusion getByRegApplicationIdLast(Integer id);

    Page<Conclusion> findFiltered(Integer reviewId, Integer id, Date dateBegin, Date dateEnd, Integer tin,Integer regionId,Integer subRegionId, String name, Category category,Integer regApplicationId, Pageable pageable);

    Conclusion create(Integer regApplicationId, String text, Integer createdById);

    Conclusion update(Conclusion conclusion, String text, Integer createdById);

    Conclusion complete(Integer conclusionId);

    List<Conclusion> getByRegApplicationId(Integer regApplicationId);
    Conclusion getTop1ByRegApplicationId(Integer regApplicationId);

    Conclusion findByConclusionNumber(String regNumber);
}
