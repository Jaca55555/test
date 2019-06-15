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
public enum PerformerStatus {

    Initial(0,"sys_performerStatus.initial"),
    Approved(1,"sys_performerStatus.approved"),
    Denied(2,"sys_performerStatus.denied");

    private Integer id;
    private String name;

    PerformerStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, PerformerStatus> performerStatusMap;
    static {
        performerStatusMap = new HashMap<>();
        for (PerformerStatus performerStatus : PerformerStatus.values()) {
            performerStatusMap.put(performerStatus.getId(), performerStatus);
        }
    }

    public static PerformerStatus getPerformerStatus(Integer id) {
        return performerStatusMap.get(id);
    }

    public static List<PerformerStatus> getPerformerStatusList() {
        List<PerformerStatus> performerStatusList = new LinkedList<>();
        for (PerformerStatus performerStatus : PerformerStatus.values()) {
            performerStatusList.add(performerStatus);
        }
        return performerStatusList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
