package uz.maroqand.ecology.core.entity.user;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import uz.maroqand.ecology.core.entity.sys.Organization;
import uz.maroqand.ecology.core.entity.sys.Soato;
import uz.maroqand.ecology.core.service.sys.SoatoService;
import uz.maroqand.ecology.core.service.user.DepartmentService;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 22.09.2017.
 */
@Data
@Entity
@Table(name = "sys_department")
public class Department {

    @Transient
    private static final String sequenceName = "sys_department_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Transient
    private List<Department> nodes;

    @Transient
    private Department parent;

    @Column(name = "parent_id")
    private Integer parentId;

    //Organization.ID
    @Column(name = "organization_id")
    private Integer organizationId;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;

    private String name;

    @Column(name = "name_ru")
    private String nameRu;

    /*
    * Technical Fields
    */
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;

    // (uz) qo'shimcha ma'lumot
    // (ru) больше информации
    private String message;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @Column(name="updated_at", columnDefinition = "timestamp without time zone")
    private Date updatedAt;

    @Column(name = "created_by_id")
    private Integer createdById;

    @Column(name = "updated_by_id")
    private Integer updatedById;

    @Override
    public String toString() {
        return "{tags:" + id + ", text:\'" + name + "\', nodes:"
                + nodes + "}";
    }

    public void addChild(Department child) {
        if(nodes==null) nodes = new LinkedList<>();
        if (!this.nodes.contains(child) && child != null)
            this.nodes.add(child);
    }

    public String getParentName(DepartmentService departmentService){

        if (parentId==null)  return "";
        Department department = departmentService.getById(parentId);
        return department!=null?department.name:"";
    }

    public String getNameTranslation(String locale) {
        switch (locale) {
            case "ru":
                return nameRu;

            default:
                return name;
        }
    }

}