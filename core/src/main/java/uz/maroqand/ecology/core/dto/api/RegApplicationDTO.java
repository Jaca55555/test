package uz.maroqand.ecology.core.dto.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import uz.maroqand.ecology.core.entity.expertise.BoilerCharacteristics;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationCategoryType;
import uz.maroqand.ecology.core.service.sys.OrganizationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class RegApplicationDTO {
    private Integer id;
    private Integer objectRegionId; //Объект жойлашган вилоят
    private Integer objectSubRegionId; //Объект жойлашган туман/шахар
    private Integer reviewId; //Ташкилот номи (аризачиники)
    private String name; //Объект номи
    private Integer tin; //СТИР
    private RegApplicationCategoryType regApplicationCategoryType; //Тоифа
    private Set<Integer> materials; //Материал тури
    private Integer conclusionId; //Хулоса
    private Set<BoilerCharacteristics> boilerCharacteristics; // Моддалар тартиб раками,  Моддалар номи,  Моддалар хажми,  Моддалар улчов бирлиги

    public static RegApplicationDTO fromEntity(RegApplication model) {
        RegApplicationDTO dto = new RegApplicationDTO();
        dto.setId(model.getId());
        dto.setTin(model.getApplicant().getTin());
        dto.setReviewId(model.getReviewId());
        dto.setName(model.getName());
        dto.setObjectRegionId(model.getObjectRegionId());
        dto.setObjectSubRegionId(model.getObjectSubRegionId());
        dto.setConclusionId(model.getConclusionId());
        dto.setMaterials(model.getMaterials());
        dto.setBoilerCharacteristics(model.getBoilerCharacteristics());
        return dto;
    }

    public static List<RegApplicationDTO> listFromEntity(List<RegApplication> models) {
        List<RegApplicationDTO> dtos = new ArrayList<>();
        for (RegApplication model: models) {
            dtos.add(fromEntity(model));
        }
        return dtos;
    }
}
