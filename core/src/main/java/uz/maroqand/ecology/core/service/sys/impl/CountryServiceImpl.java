package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.Country;
import uz.maroqand.ecology.core.repository.sys.CountryRepository;
import uz.maroqand.ecology.core.service.sys.CountryService;

import java.util.List;

/**
 * Created by Utkirbek Boltaev on 26.05.2019.
 * (uz)
 * (ru)
 */
@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Cacheable(value = "countryById", key = "#id", condition="#id != null", unless="#result == null")
    public Country getById(Integer id) throws IllegalArgumentException {
        if(id==null) return null;
        return countryRepository.getOne(id);
    }

    @Cacheable(value = "getUzbekistan")
    public Country getUzbekistan() throws IllegalArgumentException {
        return countryRepository.getOne(860);
    }

    //List
    @Cacheable(value = "getCountriesList", key = "#locale", condition="#locale != null", unless="#result == null")
    public List<Country> getCountriesList(String locale) {
        switch (locale) {
            case "en": return countryRepository.findAllByOrderByShortNameEnAsc();
            case "uz": return countryRepository.findAllByOrderByShortNameUzAsc();
            case "ru":
            default:
                return countryRepository.findAllByOrderByShortNameAsc();
        }
    }
}