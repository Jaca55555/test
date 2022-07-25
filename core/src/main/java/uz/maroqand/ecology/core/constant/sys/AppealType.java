package uz.maroqand.ecology.core.constant.sys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum AppealType {

    Applications(0,"appeal_type.applications"),
    Suggestions(1,"appeal_type.suggestions"),
    Complaints(2,"appeal_type.complaints"),
    Landing_Suggestion(3,"appeal_type.landing_suggestion");

    private Integer id;
    private String name;

    AppealType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, AppealType> appealTypeMap;
    static {
        appealTypeMap = new HashMap<>();
        for (AppealType appealType : AppealType.values()) {
            appealTypeMap.put(appealType.getId(), appealType);
        }
    }

    public static AppealType getAppealType(Integer id) {
        return appealTypeMap.get(id);
    }

    public static List<AppealType> getAppealTypeList() {
        List<AppealType> appealTypeList = new LinkedList<>();
        for (AppealType appealType : AppealType.values()) {
            appealTypeList.add(appealType);
        }
        return appealTypeList;
    }

    public static List<AppealType> getAppealTypeListWithoutOne() {
        List<AppealType> appealTypeList = new LinkedList<>();
        for (AppealType appealType : AppealType.values()) {
            if (appealType.getId()!=3) {
                appealTypeList.add(appealType);
            }
        }
        return appealTypeList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
