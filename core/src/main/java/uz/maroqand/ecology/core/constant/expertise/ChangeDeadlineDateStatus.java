package uz.maroqand.ecology.core.constant.expertise;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public enum ChangeDeadlineDateStatus {

    Initial(0,"sys_deadline_date_status_initial"),
    Approved(1,"sys_deadline_date_status_approved"),
    Denied(2,"sys_deadline_date_status_denied");

    private Integer id;
    private String name;

    ChangeDeadlineDateStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, ChangeDeadlineDateStatus> changeDeadlineDateStatusMap;
    static {
        changeDeadlineDateStatusMap = new HashMap<>();
        for (ChangeDeadlineDateStatus deadlineDateStatus : ChangeDeadlineDateStatus.values()) {
            changeDeadlineDateStatusMap.put(deadlineDateStatus.getId(), deadlineDateStatus);
        }
    }

    public static ChangeDeadlineDateStatus getChangeDeadlineDateStatus(Integer id) {
        return changeDeadlineDateStatusMap.get(id);
    }

    public static List<ChangeDeadlineDateStatus> getChangeDeadlineDateStatusList() {
        List<ChangeDeadlineDateStatus> deadlineDateStatuses = new LinkedList<>();
        for (ChangeDeadlineDateStatus deadlineDateStatus : ChangeDeadlineDateStatus.values()) {
            deadlineDateStatuses.add(deadlineDateStatus);
        }
        return deadlineDateStatuses;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}