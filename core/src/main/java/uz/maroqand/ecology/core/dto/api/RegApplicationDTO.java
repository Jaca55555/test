package uz.maroqand.ecology.core.dto.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import uz.maroqand.ecology.core.entity.expertise.BoilerCharacteristics;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.expertise.RegApplicationCategoryType;
import uz.maroqand.ecology.core.entity.sys.File;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.sys.FileService;
import uz.maroqand.ecology.core.service.sys.OrganizationService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class RegApplicationDTO {
    private Integer id;
    private Integer subjectType; //Subyekt turi(yuridik/jismoniy)
    private String name;
    private String director;
    private Integer tin;
    private String oked;
    private String account;
    private String mfo;
    private String bankName;
    private Integer subjectActivities; //subyekt faoliyat turi
    private Integer subjectCategories; //subyekt kategoriyasi
    private Integer registerArea; //ro'yhatdan o'tgan tuman
    private String registerAddress; //ro'yhatdan o'tgan manzil
    private String phone;
    private String email;
    //normativ hujjatlar
    private Integer standartDocType; //normativ hujjat turi
    private String docNumber;
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date docDate; //normativ hujjat sanasi
    private Date validityPeriod; //normativ hujjat amal qilish muddati
    private String fileStr;
    private Integer fileId;

    public static RegApplicationDTO fromEntity(RegApplication model, ConclusionService conclusionService, FileService fileService) throws IOException {
        RegApplicationDTO dto = new RegApplicationDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        if(model.getApplicant()!=null){

            dto.setSubjectType(model.getApplicant().getType().getId());
            if(model.getApplicant().getType().getId()==1){
                dto.setDirector(model.getApplicant().getDirectorFullName());
            }else{
                dto.setDirector(model.getApplicant().getName());
            }
            dto.setTin(model.getApplicant().getTin());
            dto.setOked(model.getApplicant().getOked());
            dto.setAccount(model.getApplicant().getBankAccount());
            dto.setMfo(model.getApplicant().getMfo());
            dto.setBankName(model.getApplicant().getBankName());

            dto.setRegisterArea(model.getApplicant().getSubRegionId());
            dto.setRegisterAddress(model.getApplicant().getAddress());
            dto.setPhone(model.getApplicant().getMobilePhone());
            dto.setEmail(model.getApplicant().getEmail());

        }

        dto.setSubjectActivities(model.getActivityId());
        dto.setSubjectCategories(model.getCategory().getId());
        //normativ hujjatlar
        dto.setStandartDocType(8);
        if(model.getConclusionId()!=null){
            dto.setDocNumber(conclusionService.getById(model.getConclusionId()).getNumber());
            dto.setDocDate(conclusionService.getById(model.getConclusionId()).getDate());
            dto.setValidityPeriod(conclusionService.getById(model.getConclusionId()).getDate());
            dto.setFileId(conclusionService.getById(model.getConclusionId()).getConclusionWordFileId());
            //File
            if(conclusionService.getById(model.getConclusionId()).getConclusionWordFileId()!=null){
                File file = fileService.findById(conclusionService.getById(model.getConclusionId()).getConclusionWordFileId());
                String filePath = file.getPath();
                String originalFileName = file.getName();
                byte[] input_file = Files.readAllBytes(Paths.get(filePath+originalFileName));
                byte[] encodedBytes = Base64.getEncoder().encode(input_file);
                String encodedString =  new String(encodedBytes);
                dto.setFileStr(encodedString);
            }else{
                dto.setFileStr(conclusionService.getById(model.getConclusionId()).getHtmlText());
            }
            //FileEnd
        }


        return dto;
    }

}
