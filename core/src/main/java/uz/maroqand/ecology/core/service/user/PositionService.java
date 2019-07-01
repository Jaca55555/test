package uz.maroqand.ecology.core.service.user;

import uz.maroqand.ecology.core.entity.user.Position;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz)
 * (ru)
 */
public interface PositionService {

    Position getById(Integer id);

    Position save(Position position);

    List<Position> getAll();

}
