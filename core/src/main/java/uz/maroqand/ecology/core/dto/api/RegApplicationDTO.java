package uz.maroqand.ecology.core.dto.api;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.sys.FileService;
import java.io.IOException;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class RegApplicationDTO {
    private SubjectDTO subject;
    //normativ hujjatlar
    private DocumentDTO document;
    public static RegApplicationDTO fromEntity(RegApplication model, ConclusionService conclusionService, FileService fileService) {
        RegApplicationDTO dto = new RegApplicationDTO();
        dto.setSubject(SubjectDTO.fromEntity(model));
        //normativ hujjatlar
        dto.setDocument(DocumentDTO.fromEntity(model,conclusionService,fileService));


        return dto;
    }

}
