package uz.maroqand.ecology.docmanagement.constant;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 14.02.2020.
 * (uz)
 * (ru)
 */
public enum TaskStatus {
    Initial(0,"task_sub_type.initial"),//
    New(1,"task_sub_type.new"),//Кўрилмаган
    InProgress(2,"task_sub_type.inProgress"),//Жараёнда,
    Checking(3,"task_sub_type.checking"),//Назоратдан чиқариш
    Complete(4,"task_sub_type.complete");//Якунланган

    private Integer id;
    private String name;

    TaskStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<TaskStatus> getTaskStatusList() {
        List<TaskStatus> taskStatusList = new LinkedList<>();
        for (TaskStatus taskStatus : TaskStatus.values()) {
            taskStatusList.add(taskStatus);
        }
        return taskStatusList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}