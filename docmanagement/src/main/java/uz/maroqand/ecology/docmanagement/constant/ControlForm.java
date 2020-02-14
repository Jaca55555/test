package uz.maroqand.ecology.docmanagement.constant;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 13.02.2020.
 * (uz) Назорат карточкасининг шакли
 */
public enum ControlForm {
    FormNot(0,"sys_control_form.not"),
    Form1(1,"sys_control_form.a1"),
    Form2(2,"sys_control_form.a2"),
    Form3(3,"sys_control_form.a3"),
    Form4(4,"sys_control_form.a4");

    private Integer id;
    private String name;

    ControlForm(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<ControlForm> getControlFormList() {
        return new LinkedList<>(Arrays.asList(ControlForm.values()));
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}