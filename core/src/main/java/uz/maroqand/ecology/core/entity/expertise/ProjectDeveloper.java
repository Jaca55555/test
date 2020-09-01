package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;
import uz.maroqand.ecology.core.entity.client.Opf;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Created by Utkirbek Boltaev on 14.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "project_developer")
public class ProjectDeveloper {

    @Transient
    private static final String sequenceName = "project_developer_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    //ИНН разработчика проекта
    @Size(max = 9)
    private String tin;

    //Наименование разработчика проекта
    @Column
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opf_id", updatable = false, insertable = false)
    private Opf opf;

    //Организационно правовая форма предприятия
    @Column(name = "opf_id")
    private Integer opfId;

}
