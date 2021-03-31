package uz.maroqand.ecology.cabinet.constant.expertise;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import uz.maroqand.ecology.core.entity.expertise.BoilerCharacteristics;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationCategoryType;

import java.util.Set;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class RegApplicationDTO {

    private Integer objectRegionId; //Объект жойлашган вилоят
    private Integer objectSubRegionId; //Объект жойлашган туман/шахар
    private Integer reviewId; //Ташкилот номи (аризачиники)
    private Integer objectId; //Объект номи
    private String projectDeveloperTin; //СТИР
    private RegApplicationCategoryType regApplicationCategoryType; //Тоифа
    private Set<Integer> materials; //Материал тури
    private Integer conclusionId; //Хулоса
    private Set<BoilerCharacteristics> boilerCharacteristics; // Моддалар тартиб раками,  Моддалар номи,  Моддалар хажми,  Моддалар улчов бирлиги

}
