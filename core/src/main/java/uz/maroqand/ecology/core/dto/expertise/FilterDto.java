package uz.maroqand.ecology.core.dto.expertise;

import lombok.Data;

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
    private String applicationId;
    private Long regionId;
    private Long subRegionId;
    private String regDateBegin;
    private String regDateEnd;

    private Integer activityId;
    private Integer objectId;
    private String deadlineDateBegin;
    private String deadlineDateEnd;

    private Integer confirmStatus;
    private Integer forwardingStatus;
    private Integer performerStatus;
    private Integer agreementStatus;
    private Integer agreementCompleteStatus;

    private Integer status;
    private String dateBegin;
    private String dateEnd;

}
