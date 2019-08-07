package uz.maroqand.ecology.core.entity.sys;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Utkirbek Boltaev on 07.08.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "sys_gnk_soato")
public class GNKSoato {

    @Id
    private Integer id;

    @Column(name = "soato_id")
    private Integer soatoId;

    @Column(name = "gnk_soato_id")
    private Integer gnkSoatoId;

    @Column(name = "soato_name")
    private String soatoName;

    @Column(name = "gnk_soato_name")
    private String gnkSoatoName;

}
