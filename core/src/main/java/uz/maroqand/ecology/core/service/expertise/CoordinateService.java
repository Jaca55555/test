package uz.maroqand.ecology.core.service.expertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.expertise.Coordinate;

import java.util.Date;

public interface CoordinateService {
    public Page<Coordinate> findFiltered(Integer id, Integer tin, String name, String number, Integer regionId, Integer subRegionId, Date dateBegin, Date dateEnd, Pageable pageable);
}
