package uz.maroqand.ecology.core.service.sys;


import uz.maroqand.ecology.core.entity.sys.Country;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 26.05.2019.
 * (uz)
 * (ru)
 */
public interface CountryService {

    Country getById(Integer id);

    Country getUzbekistan();

    List<Country> getCountriesList(String locale);

}
