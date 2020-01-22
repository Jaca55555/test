package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.expertise.Material;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public interface MaterialService {

    Material save(Material material);

    Material getById(Integer id);

    Page<Material> getAll(Pageable pageable);

    List<Material> getList();

    List<Material> updateList();

    Material delete(Material material, Integer userId, String msg);

}
