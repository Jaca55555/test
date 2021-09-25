package uz.maroqand.ecology.core.dto.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
public class SubjectDTO {
    private Integer subjectTypes; //Subyekt turi(yuridik/jismoniy)
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

    public static SubjectDTO fromEntity(RegApplication model){
        SubjectDTO dto = new SubjectDTO();
        dto.setName(model.getName());
        if(model.getApplicant()!=null){

            dto.setSubjectTypes(model.getApplicant().getType().getId());
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
        if (model.getCategory() != null) {
            dto.setSubjectCategories(model.getCategory().getId());
        }
        return dto;
    }

}
