package uz.maroqand.ecology.core.service.expertise;


import uz.maroqand.ecology.core.entity.expertise.AirPool;

public interface AirPoolService {

    AirPool save(AirPool airPool);

    AirPool getById(Integer id);

}
