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
    private Integer inn;
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

        dto.setSubjectTypes(model.getApplicantType().getId());
        if(model.getApplicant().getType().getId()==0){
            dto.setName(model.getApplicant().getName());
        }else if(model.getApplicant().getType().getId()==1) {
            dto.setName(model.getApplicant().getName());
        }

        if(model.getApplicant()!=null){

            dto.setSubjectTypes(model.getApplicant().getType().getId());
            if(model.getApplicant().getType().getId()==0){
                dto.setDirector(model.getApplicant().getDirectorFullName());
            }else if(model.getApplicant().getType().getId()==1) {
                dto.setDirector(model.getApplicant().getName());
            }
            dto.setInn(model.getApplicant().getTin());
            dto.setOked(model.getApplicant().getOked());
            dto.setAccount(model.getApplicant().getBankAccount());
            dto.setMfo(model.getApplicant().getMfo());
            dto.setBankName(model.getApplicant().getBankName().replace("\""," "));

            dto.setRegisterArea(model.getApplicant().getSubRegionId());
            dto.setRegisterAddress(model.getApplicant().getAddress());
            dto.setPhone(model.getApplicant().getMobilePhone());
            dto.setEmail(model.getApplicant().getEmail());

        }

        dto.setSubjectActivities(1);
        if (model.getCategory() != null) {
            dto.setSubjectCategories(model.getCategory().getId());
        }
        return dto;
    }

}
