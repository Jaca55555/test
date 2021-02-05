package uz.maroqand.ecology.core.constant.user;

import java.util.*;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 * (ru)
 */
public enum Permissions {
    //admin
    ADMIN(0, 1, "sys_permissions.admin"),
    ADMIN_ROLE(1, 1, "sys_permissions.role"),
    ADMIN_USER(2, 1, "sys_permissions.user"),

    //eco_expertise
    EXPERTISE(10, 2, "sys_permissions.eco_expertise"),
    EXPERTISE_USER(11, 2, "sys_permissions.eco_expertise_user"),//user qo'shish o'zgartirishga dostup
    EXPERTISE_CONFIRM(12, 2, "sys_permissions.expertise_confirm"),// Arizani tasdiqash
    EXPERTISE_FORWARDING(13, 2, "sys_permissions.expertise_forwarding"),// Ariza ko'rb chiquvchini tasdilash, va natijani tasdiqalsh
    EXPERTISE_PERFORMER(14, 2, "sys_permissions.expertise_performer"),// Ariza natijani kirituvchi (ijrochi)
    EXPERTISE_AGREEMENT(15, 2, "sys_permissions.expertise_agreement"),// Ariza natijasini tasdiqlash
    EXPERTISE_AGREEMENT_COMPLETE(16, 2, "sys_permissions.expertise_agreement_complete"), // Ariza natijasini tasdiqlash va ariza tugatish
    EXPERTISE_CONCLUSION_COMPLETE(52, 2, "sys_permissions.expertise_conclusion_complete"), // Ariza Rais tasdiqlagandan keyin yakunlash uchun Xulosaga raqam va sana qo'yish

    ENTERPRISE_REGISTER(17, 2, "sys_permissions.enterprise_register"), //
    BILLING(18, 2, "sys_permissions.billing"), //
    PAYMENT_FILE(19, 2, "sys_permissions.payment_file"), //
    PAYMENT_FILE_ALL(53, 2, "sys_permissions.payment_file_all"), //
    COORDINATE_REGISTER(20, 2, "sys_permissions.coordinate_register"), //

    APPEAL_ADMIN(21, 2, "sys_permissions.appeal_admin"), //
    EMPLOYEE_CONTROL(22, 2, "sys_permissions.employee_control"), //

    EXPERTISE_REG(23, 2, "sys_permissions.expertise_reg"), //
    EXPERTISE_MONITORING(24, 2, "sys_permissions.expertise_monitoring"), //
    FACTURE_MONITORING(25, 2, "sys_permissions.facture_monitoring"),
    EXPERTISE_AGREE(26, 2, "sys_permissions.expertise_agree"), //

    //doc management
    DOC_MANAGEMENT(40, 3, "sys_permissions.docManagement"), //Электрон Хужжатлар Юритиш Тизимига кириш
    DOC_MANAGEMENT_USER(41, 3, "sys_permissions.eco_expertise_user"),//user qo'shish o'zgartirishga dostup
    DOC_MANAGEMENT_REGISTER(42, 3, "sys_permissions.docManagementRegister"), //Кирувчи ва чиқувчи хатларни рўйхатга олиш
    DOC_MANAGEMENT_LIBRARY(45, 3, "sys_permissions.docManagementLibrary"), //Кутубхона маълумотларини рўйхатга олиш
    DOC_MANAGEMENT_PERFORMER(46, 3, "sys_permissions.docManagementPerformer"), //Хужжатларни ижрога йўналтириш
    DOC_MANAGEMENT_SETTINGS(47, 3, "sys_permissions.docManagementSettings"), //Электрон Хужжатлар Юритиш Тизимининг созламаларини ўзгартириш
    DOC_MANAGEMENT_CHECKED(48,3,"sys_permissions.docManagementCheck"), //Ijro nazoratida dostup alohida//
    DOC_MANAGEMENT_CHANGE_PERFORMER(49,3,"sys_permissions.docManagementChangePerformer"),
    DOC_MANAGEMENT_REPORT_LIST(50,3,"sys_permissions.docManagementrReportList"),
    DOC_MANAGEMENT_REPORT_VIEW(51,3,"sys_permissions.docManagementrReportView");

    private Integer id;
    private Integer type;
    private String name;

    Permissions(Integer id, Integer type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    private static Map<Integer, Permissions> permissionsMap;
    static {
        permissionsMap = new HashMap<>();
        for (Permissions permissions : Permissions.values()) {
            permissionsMap.put(permissions.getId(), permissions);
        }
    }

    public static Permissions getPermissions(Integer id) {
        return permissionsMap.get(id);
    }

    public static List<Permissions> getPermissionsByType(Integer type) {
        List<Permissions> permissionsList = new ArrayList<>();
        for (Permissions permissions : Permissions.values()) {
            if(permissions.getType().equals(type)){
                permissionsList.add(permissions);
            }
        }
        return permissionsList;
    }

    public static List<Permissions> getPermissionsList() {
        List<Permissions> permissionsList = new LinkedList<>();
        for (Permissions permissions : Permissions.values()) {
            permissionsList.add(permissions);
        }
        return permissionsList;
    }

    public Integer getId() {
        return id;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

}