package uz.maroqand.ecology.core.constant.expertise;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum ApplicantType {

    LegalEntity(0,"sys_applicantType.legalEntity"),
    Individual(1,"sys_applicantType.individual"),
    IndividualEnterprise(2,"sys_applicantType.individualEnterprise"),
    ForeignIndividual(3,"sys_applicantType.foreignIndividual");

    private Integer id;
    private String name;

    ApplicantType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }


    private static Map<Integer, ApplicantType> applicantTypeMap;
    static {
        applicantTypeMap = new HashMap<>();
        for (ApplicantType applicantType : ApplicantType.values()) {
            applicantTypeMap.put(applicantType.getId(), applicantType);
        }
    }

    public static ApplicantType getApplicantType(Integer id) {
        return applicantTypeMap.get(id);
    }

    public static List<ApplicantType> getApplicantTypeList() {
        List<ApplicantType> applicantTypeList = new LinkedList<>();
        for (ApplicantType applicantType : ApplicantType.values()) {
            applicantTypeList.add(applicantType);
        }
        return applicantTypeList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
