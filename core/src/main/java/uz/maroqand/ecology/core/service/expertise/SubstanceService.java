package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.constant.expertise.SubstanceType;
import uz.maroqand.ecology.core.entity.expertise.Material;
import uz.maroqand.ecology.core.entity.expertise.Substance;

import java.util.List;

public interface SubstanceService {

    Substance save(Substance substance);

    Substance getById(Integer id);

    Page<Substance> getAll(Pageable pageable, SubstanceType type);

    List<Substance> getList();

    Substance delete(Substance substance, Integer userId);

    List<Substance> updateList();
}
