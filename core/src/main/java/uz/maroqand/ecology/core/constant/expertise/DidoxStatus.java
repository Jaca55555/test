package uz.maroqand.ecology.core.constant.expertise;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum DidoxStatus {
    Pending(0,"didox_status.pending"),
    Error(1,"didox_status.error"),
    Signed(2,"didox_status.signed");
    private Integer id;
    private String name;

    DidoxStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }


    private static Map<Integer, DidoxStatus> didoxStatusMap;
    static {
        didoxStatusMap = new HashMap<>();
        for (DidoxStatus didoxStatus : DidoxStatus.values()) {
            didoxStatusMap.put(didoxStatus.getId(), didoxStatus);
        }
    }

    public static DidoxStatus getDidoxType(Integer id) {
        return didoxStatusMap.get(id);
    }

    public static List<DidoxStatus> getDidoxStatusList() {
        List<DidoxStatus> didoxStatusList = new LinkedList<>();
        for (DidoxStatus didoxStatus : DidoxStatus.values()) {
            didoxStatusList.add(didoxStatus);
        }
        return didoxStatusList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
