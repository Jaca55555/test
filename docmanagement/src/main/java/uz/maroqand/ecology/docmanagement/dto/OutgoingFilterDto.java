package uz.maroqand.ecology.docmanagement.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class OutgoingFilterDto {
    Integer documentOrganizationId;
    Date begin;
    Date end;
    String content;
    List<Integer> departmentIds;
    List<Integer> documentViewIds;
    Integer docTypeId;
}
