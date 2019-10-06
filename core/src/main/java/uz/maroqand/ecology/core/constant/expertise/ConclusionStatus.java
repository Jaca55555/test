package uz.maroqand.ecology.core.constant.expertise;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 07.10.2019.
 * (uz)
 * (ru)
 */
public enum ConclusionStatus {

    Initial(0,"sys_confirmStatus.initial"),
    Active(1,"sys_confirmStatus.returned"),
    Expired(1,"sys_confirmStatus.returned");

    private Integer id;
    private String name;

    ConclusionStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, ConclusionStatus> conclusionStatusMap;
    static {
        conclusionStatusMap = new HashMap<>();
        for (ConclusionStatus conclusionStatus : ConclusionStatus.values()) {
            conclusionStatusMap.put(conclusionStatus.getId(), conclusionStatus);
        }
    }

    public static ConclusionStatus getConclusionStatus(Integer id) {
        return conclusionStatusMap.get(id);
    }

    public static List<ConclusionStatus> getConclusionStatusList() {
        List<ConclusionStatus> conclusionStatusList = new LinkedList<>();
        for (ConclusionStatus conclusionStatus : ConclusionStatus.values()) {
            conclusionStatusList.add(conclusionStatus);
        }
        return conclusionStatusList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
