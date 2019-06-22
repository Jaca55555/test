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
public enum LogType {

    Confirm(0,"sys_logType.confirm"),
    Forwarding(1,"sys_logType.forwarding"),
    Performer(2,"sys_logType.performer"),
    Agreement(3,"sys_logType.agreement"),
    AgreementComplete(4,"sys_logType.agreementComplete");

    private Integer id;
    private String name;

    LogType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, LogType> logTypeMap;
    static {
        logTypeMap = new HashMap<>();
        for (LogType logType : LogType.values()) {
            logTypeMap.put(logType.getId(), logType);
        }
    }

    public static LogType getLogType(Integer id) {
        return logTypeMap.get(id);
    }

    public static List<LogType> getLogTypeList() {
        List<LogType> logTypeList = new LinkedList<>();
        for (LogType logType : LogType.values()) {
            logTypeList.add(logType);
        }
        return logTypeList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
