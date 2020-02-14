package uz.maroqand.ecology.docmanagement.constant;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 13.02.2020.
 * (uz) Хатнинг ижроси шакли
 */
public enum ExecuteForm {
    Performance(0,"sys_execute_form.performance"),
    Information(1,"sys_execute_form.information");

    private Integer id;
    private String name;

    ExecuteForm(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, ExecuteForm> executeFormMap;
    static {
        executeFormMap = new HashMap<>();
        for (ExecuteForm executeForm : ExecuteForm.values()) {
            executeFormMap.put(executeForm.getId(), executeForm);
        }
    }

    public static ExecuteForm getExecuteForm(Integer id) {
        return executeFormMap.get(id);
    }

    public static List<ExecuteForm> getExecuteFormList() {
        List<ExecuteForm> executeFormList = new LinkedList<>();
        for (ExecuteForm executeForm : ExecuteForm.values()) {
            executeFormList.add(executeForm);
        }
        return executeFormList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}