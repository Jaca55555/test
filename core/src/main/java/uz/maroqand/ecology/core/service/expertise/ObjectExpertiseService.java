package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.expertise.ObjectExpertise;

import java.util.List;

public interface ObjectExpertiseService {

    List<ObjectExpertise> getList();

    ObjectExpertise getById(Integer id);

    ObjectExpertise save(ObjectExpertise objectExpertise);

    Page<ObjectExpertise> findFiltered(Integer id, String name, String nameOz,String nameEn,String nameRu, Pageable pageable);

}
