package uz.maroqand.ecology.docmanagement.constant;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 14.02.2020.
 * (uz)
 * (ru)
 */
public enum TaskSubStatus {
    Initial(0,"task_sub_status.initial"),//
    New(1,"task_sub_status.new"),//Кўрилмаган

    InProgress(2,"task_sub_status.inProgress"),//Жараёнда, (listda o'zfartirsa bo'ladi)
    Waiting(3,"task_sub_status.waiting"),//Маълумот сўралган, (listda o'zfartirsa bo'ladi)
    Agreement(4,"task_sub_status.agreement"),//Келишув жараёнида, (listda o'zfartirsa bo'ladi)

    Checking(5,"task_sub_status.checking"),//Назоратдан чиқариш
    Complete(6,"task_sub_status.complete");//Якунланган

    private Integer id;
    private String name;

    TaskSubStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<TaskSubStatus> getTaskSubStatusList() {
        List<TaskSubStatus> taskSubStatusList = new LinkedList<>();
        for (TaskSubStatus taskSubStatus : TaskSubStatus.values()) {
            taskSubStatusList.add(taskSubStatus);
        }
        return taskSubStatusList;
    }

    private static Map<Integer, TaskSubStatus> taskStatusMap;
    static {
        taskStatusMap = new HashMap<>();
        for (TaskSubStatus taskStatus : TaskSubStatus.values()) {
            taskStatusMap.put(taskStatus.getId(), taskStatus);
        }
    }

    public static TaskSubStatus getTaskStatus(Integer id) {
        return taskStatusMap.get(id);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
