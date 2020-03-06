package uz.maroqand.ecology.docmanagement.constant;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Akmal Sadullayev on 03.03.2020.
 * (uz)
 * (ru)
 */
public enum DocumentLogType {

    Comment(1,"sys_logType.comment"),
    Log(2,"sys_logType.log");

    private Integer id;
    private String name;

    DocumentLogType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, DocumentLogType> logTypeMap;
    static {
        logTypeMap = new HashMap<>();
        for (DocumentLogType logType : DocumentLogType.values()) {
            logTypeMap.put(logType.getId(), logType);
        }
    }

    public static DocumentLogType getLogType(Integer id) {
        return logTypeMap.get(id);
    }

    public static List<DocumentLogType> getLogTypeList() {
        List<DocumentLogType> logTypeList = new LinkedList<>();
        for (DocumentLogType logType : DocumentLogType.values()) {
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
