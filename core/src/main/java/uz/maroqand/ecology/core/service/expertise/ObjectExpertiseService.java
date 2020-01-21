package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.expertise.ObjectExpertise;

import java.util.List;

public interface ObjectExpertiseService {

    ObjectExpertise save(ObjectExpertise objectExpertise);

    ObjectExpertise getById(Integer id);

    List<ObjectExpertise> getList();

    List<ObjectExpertise> updateList();

    Page<ObjectExpertise> findFiltered(Integer id, String name, String nameOz,String nameEn,String nameRu, Pageable pageable, Boolean deleted);

    ObjectExpertise delete(ObjectExpertise objectExpertise, Integer userId, String msg);

}
