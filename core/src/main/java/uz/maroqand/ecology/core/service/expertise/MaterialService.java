package uz.maroqand.ecology.core.service.expertise;

import uz.maroqand.ecology.core.entity.expertise.Material;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public interface MaterialService {

    Material getById(Integer id);

    List<Material> getList();

}
