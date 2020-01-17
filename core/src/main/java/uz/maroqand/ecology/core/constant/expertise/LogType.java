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

    Confirm(0,"sys_logType.confirm","sys_logType.confirmHistory"),
    Forwarding(1,"sys_logType.forwarding","sys_logType.forwardingHistory"),
    Performer(2,"sys_logType.performer","sys_logType.performerHistory"),
    Agreement(3,"sys_logType.agreement","sys_logType.agreementHistory"),
    AgreementComplete(4,"sys_logType.agreementComplete","sys_logType.agreementCompleteHistory");

    private Integer id;
    private String name;
    private String historyName;

    LogType(Integer id, String name, String historyName) {
        this.id = id;
        this.name = name;
        this.historyName = historyName;
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

    public String getHistoryName() {
        return historyName;
    }
}
