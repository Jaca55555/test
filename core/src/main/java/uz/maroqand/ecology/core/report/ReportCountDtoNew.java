package uz.maroqand.ecology.core.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportCountDtoNew {
    //region
    private HashMap<Integer, HashMap<Integer, Integer>> appealRegionByTypeMap;
    private HashMap<Integer, HashMap<Integer, Integer>> appealTaskRegionByTypeMap;
    private HashMap<Integer, HashMap<Integer, Integer>> mobileReceptionByTypeMap;

    private HashMap<Integer, Integer> totalAppealReportRegionByType;
    private HashMap<Integer, Integer> totalAppealTaskReportRegionByType;
    private HashMap<Integer, Integer> totalMobileReceptionByType;

    //organization
    private HashMap<Integer, Integer> totalAppealTaskOrganizationByType;
    private HashMap<Integer, Integer> totalAppealOrganizationByType;

    private HashMap<Integer, HashMap<Integer, Integer>> appealTaskOrganizationMap;
    private HashMap<Integer, HashMap<Integer, Integer>> appealOrganizationMap;


    ///soato and classifier
    private HashMap<Integer, HashMap<Integer, Integer>> classifierTotalAppealTaskHashMap;
    private HashMap<Integer, Integer> totalAppealTaskByClassifierType;



}
