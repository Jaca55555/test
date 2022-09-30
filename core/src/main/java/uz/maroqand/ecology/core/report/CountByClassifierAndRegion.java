package uz.maroqand.ecology.core.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountByClassifierAndRegion {

    private Integer regionId;

    private Integer classifierId;

    private Integer appealTaskReportTypeId;

    private Integer count;
}
