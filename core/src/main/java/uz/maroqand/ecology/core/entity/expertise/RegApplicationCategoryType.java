package uz.maroqand.ecology.core.entity.expertise;

import java.util.*;

public enum RegApplicationCategoryType {
    oneToTree(0,"sys_applicant_category.oneToTree"),
    fourType(1,"sys_applicant_category.fourType");

    private Integer id;
    private String name;

    RegApplicationCategoryType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }


    private static Map<Integer, RegApplicationCategoryType> regApplicationCategoryTypeMap;
    static {
        regApplicationCategoryTypeMap = new HashMap<>();
        for (RegApplicationCategoryType categoryType : RegApplicationCategoryType.values()) {
            regApplicationCategoryTypeMap.put(categoryType.getId(), categoryType);
        }
    }

    public static RegApplicationCategoryType getRegCategoryTypeId(Integer id) {
        return regApplicationCategoryTypeMap.get(id);
    }

    public static List<RegApplicationCategoryType> getRegApplicationCategoryTypeList() {
        List<RegApplicationCategoryType> regApplicationCategoryTypeList = new LinkedList<>();
        regApplicationCategoryTypeList.addAll(Arrays.asList(RegApplicationCategoryType.values()));
        return regApplicationCategoryTypeList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
