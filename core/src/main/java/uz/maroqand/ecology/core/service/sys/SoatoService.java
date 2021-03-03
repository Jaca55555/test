package uz.maroqand.ecology.core.service.sys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.sys.Soato;

import java.util.List;
import java.util.Set;

public interface SoatoService {

    Soato getById(Integer id);

    List<Soato> getRegions();

    List<Soato> getSubRegions();
    List<Soato> getSubregionsbyregionId(Long id);
    Page<Soato> getFiltered(Integer regionId, Set<Integer> subRegionIds, Set<Integer> organizationIds, Pageable pageable);

}
