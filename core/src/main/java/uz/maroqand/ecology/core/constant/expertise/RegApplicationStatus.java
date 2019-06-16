package uz.maroqand.ecology.core.constant.expertise;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 16.06.2019.
 * (uz) Ariza status
 * (ru)
 */
public enum RegApplicationStatus {

    Initial(0,"sys_regApplication.statusInitial"), //Ma'lumotlar kiitilmoqda
    Approved(1,"sys_regApplication.statusSuccess"),
    Denied(2,"sys_regApplication.statusError");

    private Integer id;
    private String name;

    RegApplicationStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, RegApplicationStatus> regApplicationStatusMap;
    static {
        regApplicationStatusMap = new HashMap<>();
        for (RegApplicationStatus regApplicationStatus : RegApplicationStatus.values()) {
            regApplicationStatusMap.put(regApplicationStatus.getId(), regApplicationStatus);
        }
    }

    public static RegApplicationStatus getRegApplicationStatus(Integer id) {
        return regApplicationStatusMap.get(id);
    }

    public static List<RegApplicationStatus> getRegApplicationStatusList() {
        List<RegApplicationStatus> regApplicationStatusList = new LinkedList<>();
        for (RegApplicationStatus regApplicationStatus : RegApplicationStatus.values()) {
            regApplicationStatusList.add(regApplicationStatus);
        }
        return regApplicationStatusList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
