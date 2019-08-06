package uz.maroqand.ecology.core.entity.client;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
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

    @Column(name = "name_oz")
    private String nameOz;

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

    public String getNameTranslation(String locale) {
        switch (locale) {
            case "uz":
                return nameUz;
            case "oz":
                return nameOz;
            case "ru":
            default:
                return name;
        }
    }

}
