package uz.maroqand.ecology.core.constant.sys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum AppealType {

    Payment(0,"appeal_type.payment"),
    Payment1(1,"appeal_type.payment1"),
    Payment2(2,"appeal_type.payment2"),
    Payment3(3,"appeal_type.payment3");

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

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
