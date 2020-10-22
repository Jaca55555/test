package uz.maroqand.ecology.core.dto.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.constant.expertise.RegApplicationStatus;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;

import java.util.Set;

/**
 * Created by Utkirbek Boltaev on 22.06.2019.
 * (uz)
 * (ru)
 */
@Data
public class FilterDto {

    private Integer tin;
    private String name;
    private String contractNumber;
    private Integer applicationId;
    private Integer regionId;
    private Integer ecoRegionId;//xudud qaysiligi
    private Integer subRegionId;
    private String regDateBegin;
    private String regDateEnd;

    private Integer activityId;
    private Integer objectId;
    private String deadlineDateBegin;
    private String deadlineDateEnd;
    private Integer status;
    private Set<RegApplicationStatus> statusForReg;
    private Boolean conclusionOnline;

    private String dateBegin;
    private String dateEnd;

}
