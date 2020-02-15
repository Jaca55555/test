package uz.maroqand.ecology.docmanagement.constant;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 14.02.2020.
 * (uz)
 * (ru)
 */
public enum TaskSubType {
    Performer(0,"task_sub_type.performer"),//Ижрочи
    PerformerPartner(1,"task_sub_type.performer_partner"),//Хамкор ижрочи
    Control(2,"task_sub_type.control"),//Ижро назорати
    Info(3,"task_sub_type.info");//Маълумот учун

    private Integer id;
    private String name;

    TaskSubType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<TaskSubType> getTaskSubTypeList() {
        List<TaskSubType> taskSubTypeList = new LinkedList<>();
        for (TaskSubType taskSubType : TaskSubType.values()) {
            taskSubTypeList.add(taskSubType);
        }
        return taskSubTypeList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
