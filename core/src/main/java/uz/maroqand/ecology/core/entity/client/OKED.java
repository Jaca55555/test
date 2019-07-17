package uz.maroqand.ecology.core.entity.client;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sys_oked")
public class OKED {
    @Id
    private Integer id;

    // Секция
    @Column(name = "section_name")
    private String sectionName;

    // Раздел
    @Column(name = "division_name")
    private String divisionName;

    // Группа
    @Column(name = "group_name")
    private String groupName;

    // Класс
    @Column(name = "type_name")
    private String typeName;

    // Подкласс
    @Column(name = "subtype_name")
    private String subtypeName;

    // Наименование
    private String name;

    @Column(name = "name_uz")
    private String nameUz;

    // Среднегодовая численность работников: микрофирмы
    @Column(name = "mini_workers_count")
    private String miniCompanyWorkersCount;

    // Среднегодовая численность работников: малые предприятия
    @Column(name = "medium_workers_count")
    private String mediumCompanyWorkersCount;

    private Integer level;

    @Column(name = "parent_section_id")
    private Integer parentSectionId;

    @Column(name = "parent_division_id")
    private Integer parentDivisionId;

    @Column(name = "parent_group_id")
    private Integer parentGroupId;

    @Column(name = "parent_type_id")
    private Integer parentTypeId;

    @Column(name = "requires_licence")
    private Boolean requiresLicence;

//    @Column(name = "parent_subtype_id")
//    private Integer parentSubtypeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSubtypeName() {
        return subtypeName;
    }

    public void setSubtypeName(String subtypeName) {
        this.subtypeName = subtypeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMiniCompanyWorkersCount() {
        return miniCompanyWorkersCount;
    }

    public void setMiniCompanyWorkersCount(String miniCompanyWorkersCount) {
        this.miniCompanyWorkersCount = miniCompanyWorkersCount;
    }

    public String getMediumCompanyWorkersCount() {
        return mediumCompanyWorkersCount;
    }

    public void setMediumCompanyWorkersCount(String mediumCompanyWorkersCount) {
        this.mediumCompanyWorkersCount = mediumCompanyWorkersCount;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getParentSectionId() {
        return parentSectionId;
    }

    public void setParentSectionId(Integer parentSectionId) {
        this.parentSectionId = parentSectionId;
    }

    public Integer getParentDivisionId() {
        return parentDivisionId;
    }

    public void setParentDivisionId(Integer parentDivisionId) {
        this.parentDivisionId = parentDivisionId;
    }

    public Integer getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(Integer parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

    public Integer getParentTypeId() {
        return parentTypeId;
    }

    public void setParentTypeId(Integer parentTypeId) {
        this.parentTypeId = parentTypeId;
    }
/*
    public Integer getParentSubtypeId() {
        return parentSubtypeId;
    }

    public void setParentSubtypeId(Integer parentSubtypeId) {
        this.parentSubtypeId = parentSubtypeId;
    }*/

    public String getClassesForFrontend() {
        if (level == 1) {
            return "oked-1";
        } else if(level == 2) {
            return "oked-2 sec-" + parentSectionId;
        } else if(level == 3) {
            return "oked-3 sec-" + parentDivisionId;
        }
        return "";
    }

    public Boolean getRequiresLicence() {
        return requiresLicence;
    }

    public void setRequiresLicence(Boolean requiresLicence) {
        this.requiresLicence = requiresLicence;
    }

    public String getNameTranslation(String locale) {
        if ("uz".equals(locale)) {
            return nameUz;
        } else {
            return name;
        }
    }
}
