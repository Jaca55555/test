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
public enum LogStatus {

    Initial(0,"sys_confirmStatus.initial","sys_forwardingStatus.initial","sys_performerStatus.initial","sys_agreementStatus.initial"),//boshlang'ich xolat(ro'yhatda ko'rinmaydi)
    New(1,"sys_confirmStatus.new","sys_forwardingStatus.new","sys_performerStatus.returned","sys_agreementStatus.new"),//yangi ariza
    Resend(2,"sys_confirmStatus.resend","sys_forwardingStatus.resend","sys_performerStatus.resend","sys_agreementStatus.resend"),//Qayta ishlash uchun kelgan ariza

    //natija statuslari
    Modification(3,"sys_confirmStatus.modification","sys_forwardingStatus.modification","sys_performerStatus.modification","sys_agreementStatus.modification"),//Qayta ishlash
    Approved(4,"sys_confirmStatus.approved","sys_forwardingStatus.approved","sys_performerStatus.approved","sys_agreementStatus.approved"),//Tasdiqlash
    Denied(5,"sys_confirmStatus.denied","sys_forwardingStatus.denied","sys_performerStatus.denied","sys_agreementStatus.denied");//Rad etish

    private Integer id;
    private String confirmName;
    private String forwardingName;
    private String performerName;
    private String agreementName;

    LogStatus(Integer id, String confirmName, String forwardingName, String performerName, String agreementName) {
        this.id = id;
        this.confirmName = confirmName;
        this.forwardingName = forwardingName;
        this.performerName = performerName;
        this.agreementName = agreementName;
    }

    private static Map<Integer, LogStatus> logStatusMap;
    static {
        logStatusMap = new HashMap<>();
        for (LogStatus logStatus : LogStatus.values()) {
            logStatusMap.put(logStatus.getId(), logStatus);
        }
    }

    public static LogStatus getLogStatus(Integer id) {
        return logStatusMap.get(id);
    }

    public static List<LogStatus> getLogStatusList() {
        List<LogStatus> logStatusList = new LinkedList<>();
        for (LogStatus logStatus : LogStatus.values()) {
            logStatusList.add(logStatus);
        }
        return logStatusList;
    }

    public Integer getId() {
        return id;
    }

    public String getConfirmName() {
        return confirmName;
    }

    public String getForwardingName() {
        return forwardingName;
    }

    public String getPerformerName() {
        return performerName;
    }

    public String getAgreementName() {
        return agreementName;
    }
}
