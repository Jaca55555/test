package uz.maroqand.ecology.core.dto.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.Category;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;

/**
 * Created by Utkirbek Boltaev on 22.08.2019.
 * (uz)
 * (ru)
 */
@Data
public class CategoryDto {

    private Integer id;
    private String name;
    private Category category;

    public CategoryDto(
            Category category,
            HelperService helperService,
            String locale
    ){
        this.category = category;
        this.id = category.getId();
        this.name = helperService.getTranslation(category.getName(), locale);
    }

}
