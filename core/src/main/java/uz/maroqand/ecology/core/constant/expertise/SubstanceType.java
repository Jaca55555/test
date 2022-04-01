package uz.maroqand.ecology.core.constant.expertise;

import uz.maroqand.ecology.core.entity.expertise.Substance;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum SubstanceType {


    SUBSTANCE_TYPE1(1,"substance_type1"),
    SUBSTANCE_TYPE2(2,"substance_type2"),
    SUBSTANCE_TYPE3(3,"substance_type3");

    private Integer id;
    private String name;

    SubstanceType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, SubstanceType> substanceTypeMap;
    static {
        substanceTypeMap = new HashMap<>();
        for (SubstanceType substanceType : SubstanceType.values()) {
            substanceTypeMap.put(substanceType.getId(), substanceType);
        }
    }

    public static SubstanceType getSubstance(Integer id) {
        return substanceTypeMap.get(id);
    }

    public static List<SubstanceType> getSubstanceTypeList() {
        List<SubstanceType> substanceTypes = new LinkedList<>();
        for (SubstanceType substanceType : SubstanceType.values()) {
            substanceTypes.add(substanceType);
        }
        return substanceTypes;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
