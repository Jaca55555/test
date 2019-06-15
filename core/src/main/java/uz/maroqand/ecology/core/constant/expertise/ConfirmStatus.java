package uz.maroqand.ecology.core.constant.expertise;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 16.06.2019.
 * (uz)
 * (ru)
 */
public enum ConfirmStatus {

    Initial(0,"sys_confirmStatus.initial"),
    Approved(1,"sys_confirmStatus.approved"),
    Denied(2,"sys_confirmStatus.denied");

    private Integer id;
    private String name;

    ConfirmStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, ConfirmStatus> confirmStatusMap;
    static {
        confirmStatusMap = new HashMap<>();
        for (ConfirmStatus confirmStatus : ConfirmStatus.values()) {
            confirmStatusMap.put(confirmStatus.getId(), confirmStatus);
        }
    }

    public static ConfirmStatus getConfirmStatus(Integer id) {
        return confirmStatusMap.get(id);
    }

    public static List<ConfirmStatus> getConfirmStatusList() {
        List<ConfirmStatus> confirmStatusList = new LinkedList<>();
        for (ConfirmStatus confirmStatus : ConfirmStatus.values()) {
            confirmStatusList.add(confirmStatus);
        }
        return confirmStatusList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
