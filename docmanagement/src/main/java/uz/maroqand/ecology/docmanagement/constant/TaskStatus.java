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
public enum TaskStatus {
    Initial(0,"task_status.initial","info"),//
    New(1,"task_status.new","warning"),//Кўрилмаган
    InProgress(2,"task_status.inProgress","primary"),//Жараёнда,
    Checking(3,"task_status.checking","secondary"),//Назоратдан чиқариш
    Complete(4,"task_status.complete","success"),//Якунланган
    Rejected(5,"task_status.rejected","danger");//Rad etildi

    private Integer id;
    private String name;
    private String color;

    TaskStatus(Integer id, String name,String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static List<TaskStatus> getTaskStatusList() {
        List<TaskStatus> taskStatusList = new LinkedList<>();
        for (TaskStatus taskStatus : TaskStatus.values()) {
            taskStatusList.add(taskStatus);
        }
        return taskStatusList;
    }
    private static Map<Integer, TaskStatus> taskStatusMap;
    static {
        taskStatusMap = new HashMap<>();
        for (TaskStatus taskStatus : TaskStatus.values()) {
            taskStatusMap.put(taskStatus.getId(), taskStatus);
        }
    }

    public static TaskStatus getTaskStatus(Integer id) {
        return taskStatusMap.get(id);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}