package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;

import javax.persistence.*;

//
@Data
@Entity
@Table(name = "description_of_sources_additional")
public class DescriptionOfSourcesAdditional {

    @Transient
    private static final String sequenceName = "description_of_sources_additional_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;


    //Атмосферага ташланадиган зарарли модда номи
    @Column
    private String name;

    //Зарарли модда миқдори
//    г/сек
    @Column(name = "g_sek", precision = 20, scale = 2)
    private Double gSek;

    //т/йил
    @Column(name = "t_yil", precision = 20, scale = 2)
    private Double tYil;

    //Установленная квота
    @Column(name = "established_quota", precision = 20, scale = 2)
    private Double establishedQuota;

    //Макс. концентрация
    @Column(name = "concentration", precision = 20, scale = 2)
    private Double concentration;

    @Column(name="deleted", columnDefinition = "boolean default false")
    private Boolean deleted;

}
