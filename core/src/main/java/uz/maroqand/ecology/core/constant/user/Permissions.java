package uz.maroqand.ecology.core.constant.user;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 * (ru)
 */
public enum Permissions {

    ADMIN(0,"sys_permissions.admin"),
    USER(1,"sys_permissions.user"),
    EXPERTISE_CONFIRM(2,"sys_permissions.expertise_confirm"),// Arizani tasdiqash
    EXPERTISE_FORWARDING(3,"sys_permissions.expertise_forwarding"),// Ariza ko'rb chiquvchini tasdilash, va natijani tasdiqalsh
    EXPERTISE_PERFORMER(4,"sys_permissions.expertise_performer"),// Ariza natijani kirituvchi (ijrochi)
    EXPERTISE_AGREEMENT(5,"sys_permissions.expertise_agreement"),// Ariza natijasini tasdiqlash
    EXPERTISE_AGREEMENT_COMPLETE(6,"sys_permissions.expertise_agreement_complete"), // Ariza natijasini tasdiqlash va ariza tugatish

    ENTERPRISE_REGISTER(7,"sys_permissions.enterprise_register"), //
    BILLING(8,"sys_permissions.billing"), //
    PAYMENT_FILE(9,"sys_permissions.payment_file"), //
    COORDINATE_REGISTER(10,"sys_permissions.coordinate_register"), //

    APPEAL_ADMIN(11,"sys_permissions.appeal_admin"), //
    EMPLOYEE_CONTROL(12,"sys_permissions.employee_control"), //

    EXPERTISE_REG(13,"sys_permissions.expertise_reg"), //
    EXPERTISE_MONITORING(14,"sys_permissions.expertise_monitoring"), //
    FACTURE_MONITORING(15,"sys_permissions.facture_monitoring"), //


    DOC_MANAGEMENT(30,"sys_permissions.docManagement"), //Электрон Хужжатлар Юритиш Тизимига кириш
    DOC_MANAGEMENT_REGISTER(31,"sys_permissions.docManagementRegister"), //Кирувчи ва чиқувчи хатларни рўйхатга олиш
    DOC_MANAGEMENT_INTERNAL(32,"sys_permissions.docManagementInternal"), //Ички хужжатларни рўйхатга олиш
    DOC_MANAGEMENT_APPEAL(33,"sys_permissions.docManagementAppeal"), //Мурожаатларни рўйхатга олиш
    DOC_MANAGEMENT_LIBRARY(34,"sys_permissions.docManagementLibrary"), //Кутубхона маълумотларини рўйхатга олиш
    DOC_MANAGEMENT_PERFORMER(35,"sys_permissions.docManagementPerformer"), //Хужжатларни ижрога йўналтириш
    DOC_MANAGEMENT_SETTINGS(36,"sys_permissions.docManagementSettings"); //Электрон Хужжатлар Юритиш Тизимининг созламаларини ўзгартириш

    private Integer id;
    private String name;

    Permissions(Integer id, String name) {
        this.id = id;
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

    public String getName() {
        return name;
    }

}