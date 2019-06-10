package uz.maroqand.ecology.core.service.sys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.sys.Translation;

/**
 * Created by Utkirbek Boltaev on 20.05.2019.
 * (uz)
 * (ru)
 */
public interface TranslationService {

    public Translation findByName(String name);

    public Translation updateByName(String name);

    public Translation getById(Integer id);

    public Translation create(Translation translation);

    public Translation update(Translation translation);

    Page<Translation> findFiltered(
            String translationTag,
            String translationRu,
            String translationUz,
            String translationEn,
            String translationOz,
            Pageable pageable
    );

}
