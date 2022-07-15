package uz.maroqand.ecology.core.constant.expertise;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 16.06.2019.
 * (uz) Ariza status
 * (ru)
 */
public enum RegApplicationStatus {

    Initial(0,"sys_regApplication.statusInitial","badge badge-secondary"), //ispolnitel  natijani kiritishni boshlagan
    CheckSent(1,"sys_regApplication.statusCheck","badge badge-info"), //ispolnitel natijani kiritgandan
    CheckConfirmed(2,"sys_regApplication.statusConfirmed","badge badge-success"), //ispolnitel natijani kiritgandan
    CheckNotConfirmed(3,"sys_regApplication.checkNotConfirmed","badge badge-danger"), //ispolnitel natijani kiritgandan

    Process(4,"sys_regApplication.statusProcess","badge badge-info"), //ispolnitel natijani kiritgandan
    Modification(5,"sys_regApplication.statusModification","badge badge-warning"), //dorabotka, qayta ko'rib chiqish
    Approved(6,"sys_regApplication.statusApproved","badge badge-success"), //ispolnitel tasdiqlash yozsa va rais tasdiqlasa
    NotConfirmed(7,"sys_regApplication.statusNotConfirmed","badge badge-danger"), //ispolnitel rad javobi yozsa va rais tasdiqlasa
    Canceled(8,"sys_regApplication.statusCanceled","badge badge-danger"),//90 kun ichida to'lov qilinmagan bo'lsa bekor qilindi
    ModificationCanceled(9,"sys_regApplication.statusModificationCanceled","badge badge-danger");

    private Integer id;
    private String name;
    private String color;

    RegApplicationStatus(Integer id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    private static Map<Integer, RegApplicationStatus> regApplicationStatusMap;
    static {
        regApplicationStatusMap = new HashMap<>();
        for (RegApplicationStatus regApplicationStatus : RegApplicationStatus.values()) {
            regApplicationStatusMap.put(regApplicationStatus.getId(), regApplicationStatus);
        }
    }

    public static RegApplicationStatus getRegApplicationStatus(Integer id) {
        return regApplicationStatusMap.get(id);
    }

    public static List<RegApplicationStatus> getRegApplicationStatusList() {
        List<RegApplicationStatus> regApplicationStatusList = new LinkedList<>();
        for (RegApplicationStatus regApplicationStatus : RegApplicationStatus.values()) {
            regApplicationStatusList.add(regApplicationStatus);
        }
        return regApplicationStatusList;
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
