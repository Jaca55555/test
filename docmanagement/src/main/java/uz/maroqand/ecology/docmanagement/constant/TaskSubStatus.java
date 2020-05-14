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
    Initial(0,"task_sub_status.initial","info"),//
    New(1,"task_sub_status.new","primary"),//Кўрилмаган

    InProgress(2,"task_sub_status.inProgress","warning"),//Жараёнда, (listda o'zfartirsa bo'ladi)
    Waiting(3,"task_sub_status.waiting","warning"),//Маълумот сўралган, (listda o'zfartirsa bo'ladi)
    Agreement(4,"task_sub_status.agreement","warning"),//Келишув жараёнида, (listda o'zfartirsa bo'ladi)

    Checking(5,"task_sub_status.checking","secondary"),//Назоратдан чиқариш
    Complete(6,"task_sub_status.complete","success"),//Якунланган
    Rejected(7,"task_sub_status.rejected","danger"),//Rad etildi
    ForChangePerformer(8,"task_sub_status.performer","light");

    private Integer id;
    private String name;
    private String color;

    TaskSubStatus(Integer id, String name,String color) {
        this.id = id;
        this.name = name;
        this.color = color;
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

    public String getColor() { return color; }
}
