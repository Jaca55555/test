package uz.maroqand.ecology.cabinet.constant.admin;

import uz.maroqand.ecology.core.entity.sys.Organization;

/**
 * Created by Utkirbek Boltaev on 07.03.2020.
 * (uz)
 * (ru)
 */
public class AdminTemplates {
    private static final String Prefix = "admin";

    //User
    private static final String User = Prefix + "/user";
    public static final String UserList = User + "/list";
    public static final String UserNew = User + "/new";
    public static final String UserPswEdit = User + "/psw_edit";
    public static final String UserView = User + "/view";

    //Department
    private static final String Department = Prefix + "/department";
    public static final String DepartmentList = Department + "/list";
    public static final String DepartmentNew = Department + "/new";
    public static final String DepartmentView = Department + "/view";

    //Position
    private static final String Position = Prefix + "/position";
    public static final String PositionList = Position + "/list";
    public static final String PositionView = Position + "/view";

    //Roles
    private static final String Roles = Prefix + "/roles";
    public static final String RolesList = Roles + "/list";
    public static final String RolesNew = Roles + "/new";
    public static final String RolesView = Roles + "/view";

    //Organization
    private static final String Organization = Prefix + "/organization";
    public static final String OrganizationList = Organization + "/list";
    public static final String OrganizationNew = Organization + "/new";
    public static final String OrganizationView = Organization + "/view";

}