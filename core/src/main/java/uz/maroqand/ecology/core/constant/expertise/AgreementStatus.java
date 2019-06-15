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
public enum AgreementStatus {

    Initial(0,"sys_agreementStatus.initial"),
    Approved(1,"sys_agreementStatus.approved"),
    Denied(2,"sys_agreementStatus.denied");

    private Integer id;
    private String name;

    AgreementStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private static Map<Integer, AgreementStatus> agreementStatusMap;
    static {
        agreementStatusMap = new HashMap<>();
        for (AgreementStatus agreementStatus : AgreementStatus.values()) {
            agreementStatusMap.put(agreementStatus.getId(), agreementStatus);
        }
    }

    public static AgreementStatus getAgreementStatus(Integer id) {
        return agreementStatusMap.get(id);
    }

    public static List<AgreementStatus> getAgreementStatusList() {
        List<AgreementStatus> agreementStatusList = new LinkedList<>();
        for (AgreementStatus agreementStatus : AgreementStatus.values()) {
            agreementStatusList.add(agreementStatus);
        }
        return agreementStatusList;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
