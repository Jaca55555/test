package uz.maroqand.ecology.docmanagement.constant;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 14.02.2020.
 * (uz)
 * (ru)
 */
public enum TaskSubStatus {
    Initial(0,"task_sub_type.initial"),//
    New(1,"task_sub_type.new"),//Кўрилмаган

    InProgress(2,"task_sub_type.inProgress"),//Жараёнда, (listda o'zfartirsa bo'ladi)
    Waiting(3,"task_sub_type.waiting"),//Маълумот сўралган, (listda o'zfartirsa bo'ladi)
    Agreement(4,"task_sub_type.agreement"),//Келишув жараёнида, (listda o'zfartirsa bo'ladi)

    Checking(5,"task_sub_type.checking"),//Назоратдан чиқариш
    Complete(6,"task_sub_type.complete");//Якунланган

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

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
