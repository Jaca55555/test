package uz.maroqand.ecology.core.service.sys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.sys.Soato;

import java.util.List;

public interface SoatoService {

    Soato getById(Integer id);

    List<Soato> getRegions();

    List<Soato> getSubRegions();
    List<Soato> getSubregionsbyregionId(Long id);
    Page<Soato> getFiltered( Pageable pageable);
}
