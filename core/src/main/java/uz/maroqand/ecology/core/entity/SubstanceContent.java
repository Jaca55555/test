package uz.maroqand.ecology.core.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "reg_application")
public class SubstanceContent {

    @Transient
    private static final String sequenceName = "substance_content_id_seq";


    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;


    @Column(name = "substance_id")
    private Integer substanceId;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "unit")
    private Integer unit;
}
