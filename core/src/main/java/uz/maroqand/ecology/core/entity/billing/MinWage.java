package uz.maroqand.ecology.core.entity.billing;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
@Data
@Entity
@Table(name = "sys_min_wage")
public class MinWage {

    @Transient
    static final String sequenceName = "sys_min_wage_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "begin_date")
    @DateTimeFormat(pattern="dd.MM.yyyy")
    private Date beginDate;

    @Column(name = "registered_at")
    @DateTimeFormat(pattern="dd.MM.yyyy")
    private Date registeredAt;

}
