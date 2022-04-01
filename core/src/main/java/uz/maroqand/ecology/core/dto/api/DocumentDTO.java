package uz.maroqand.ecology.core.dto.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.apache.commons.lang3.time.DateUtils;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.service.expertise.ConclusionService;
import uz.maroqand.ecology.core.service.sys.FileService;
import java.util.Date;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class DocumentDTO {
    private Integer standartDocTypes; //normativ hujjat turi
    private String docNumber;
    @JsonFormat(pattern="dd.MM.yyyy")
    private Date docDate; //normativ hujjat sanasi

    @JsonFormat(pattern="dd.MM.yyyy")
    private Date validityPeriod; //normativ hujjat amal qilish muddati
    private Integer organization;

    public static DocumentDTO fromEntity(RegApplication model, ConclusionService conclusionService, FileService fileService){
        DocumentDTO dto = new DocumentDTO();
        dto.setStandartDocTypes(8);
        if(model.getConclusionId()!=null){
            if (conclusionService.getById(model.getConclusionId()).getNumber() != null) {
                dto.setDocNumber(conclusionService.getById(model.getConclusionId()).getNumber());
            } else {
                dto.setDocNumber("1");
            }
            if (conclusionService.getById(model.getConclusionId()).getDate() != null) {
                dto.setDocDate(conclusionService.getById(model.getConclusionId()).getDate());
            } else {
                dto.setDocDate(new Date());
            }

            if (conclusionService.getById(model.getConclusionId()).getDate() != null) {
                dto.setValidityPeriod(DateUtils.addYears(conclusionService.getById(model.getConclusionId()).getDate(),3));
            } else {
                dto.setValidityPeriod(new Date());
            }
            if (model.getReviewId() != null) {
                dto.setOrganization(model.getReviewId());
            } else {
                dto.setOrganization(0);
            }
//            dto.setFileId(conclusionService.getById(model.getConclusionId()).getConclusionWordFileId());
            //File
//            if(conclusionService.getById(model.getConclusionId()).getConclusionWordFileId()!=null){
//                File file = fileService.findById(conclusionService.getById(model.getConclusionId()).getConclusionWordFileId());
//                String filePath = file.getPath();
//                String originalFileName = file.getName();
//                byte[] input_file = Files.readAllBytes(Paths.get(filePath+originalFileName));
//                byte[] encodedBytes = Base64.getEncoder().encode(input_file);
//                String encodedString =  new String(encodedBytes);
//                dto.setFileStr(encodedString);
//            }
//            else{
//                String htmlText = conclusionService.getById(model.getConclusionId()).getHtmlText();
//                String XHtmlText = htmlText.replaceAll("&nbsp;","&#160;");
//
//                java.io.File  pdfFile= fileService.renderPdf(XHtmlText);
//
//                byte[] bytes = Files.readAllBytes(Paths.get(pdfFile.getAbsolutePath()));
//                byte[] encodedBytes = Base64.getEncoder().encode(bytes);
//                String encodedString =  new String(encodedBytes);
//                dto.setFileStr(encodedString);
////                dto.setFileStr(conclusionService.getById(model.getConclusionId()).getHtmlText());
//            }
            //FileEnd
        }

        return dto;
    }
}
