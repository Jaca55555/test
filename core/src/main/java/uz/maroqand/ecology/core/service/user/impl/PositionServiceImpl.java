package uz.maroqand.ecology.core.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.user.Position;
import uz.maroqand.ecology.core.repository.user.PositionRepository;
import uz.maroqand.ecology.core.service.user.PositionService;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 29.03.2019.
 * (uz)
 * (ru)
 */
@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;

    @Autowired
    public PositionServiceImpl(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }


    @Override
    public Position getById(Integer id) {
        return positionRepository.findByIdAndDeletedFalse(id);
    }

    @Override
    public Position save(Position position) {
        return positionRepository.save(position);
    }

    @Override
    public List<Position> getAll() {
        return positionRepository.findAllByDeletedFalseOrderByIdDesc();
    }


}