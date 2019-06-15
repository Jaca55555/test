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
public enum ForwardingStatus {

    Initial(0,"sys_forwardingStatus.initial"),
    Approved(1,"sys_forwardingStatus.approved"),
    Denied(2,"sys_forwardingStatus.denied");

    private Integer id;
    private String name;

    ForwardingStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, ForwardingStatus> forwardingStatusMap;
    static {
        forwardingStatusMap = new HashMap<>();
        for (ForwardingStatus forwardingStatus : ForwardingStatus.values()) {
            forwardingStatusMap.put(forwardingStatus.getId(), forwardingStatus);
        }
    }

    public static ForwardingStatus getForwardingStatus(Integer id) {
        return forwardingStatusMap.get(id);
    }

    public static List<ForwardingStatus> getForwardingStatusList() {
        List<ForwardingStatus> forwardingStatusList = new LinkedList<>();
        for (ForwardingStatus forwardingStatus : ForwardingStatus.values()) {
            forwardingStatusList.add(forwardingStatus);
        }
        return forwardingStatusList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
