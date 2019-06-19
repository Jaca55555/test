package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.entity.expertise.Requirement;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public interface RequirementService {

    Requirement getById(Integer id);

    List<Requirement> getRequirementMaterials(Integer objectExpertiseId, Category category);

    List<Requirement> getAllList();

    Page<Requirement> findFiltered(
            Integer id,
            Integer objectExpertiseId,
            Category category,
            Integer materialId,
            Integer reviewId,
            Double qty,
            Integer deadline,
            Pageable pageable);

    Requirement save(Requirement requirement);

}
