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
public enum LogStep {

    Initial(0,"sys_performerStatus.initial"),
    ConfirmConfirmed(0,"sys_performerStatus.initial"),
    ConfirmNotConfirmed(0,"sys_performerStatus.initial"),

    Forwarding(0,"sys_performerStatus.initial");


    private Integer id;
    private String name;

    LogStep(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, LogStep> logStepMap;
    static {
        logStepMap = new HashMap<>();
        for (LogStep logStep : LogStep.values()) {
            logStepMap.put(logStep.getId(), logStep);
        }
    }

    public static LogStep getLogStep(Integer id) {
        return logStepMap.get(id);
    }

    public static List<LogStep> getLogStepList() {
        List<LogStep> logStepList = new LinkedList<>();
        for (LogStep logStep : LogStep.values()) {
            logStepList.add(logStep);
        }
        return logStepList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
