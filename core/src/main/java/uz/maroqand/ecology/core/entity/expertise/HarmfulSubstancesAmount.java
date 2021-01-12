package uz.maroqand.ecology.core.entity.expertise;

import lombok.Data;

import javax.persistence.*;

//Зарарли моддаларни умумий миқдори, уларни тозалаш ва фойдаланиш
@Data
@Entity
@Table(name = "harmful_substances_amount")
public class HarmfulSubstancesAmount {

    @Transient
    private static final String sequenceName = "harmful_substances_amount_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;


    //Зарарли модда номи
    @Column
    private String name;

    //Барча манбалардан чиқаётган зарарли моддалар миқдори, т/йил
    @Column(name = "substances_amount", precision = 20, scale = 2)
    private Double substancesAmount;

    //Тозалаш учун олинади
    @Column(name = "for_cleaning", precision = 20, scale = 2)
    private Double forCleaning;

    //Тутиб қолингани
    @Column(name = "caught", precision = 20, scale = 2)
    private Double caught;

    //Фойдаланилгани
    @Column(name = "used", precision = 20, scale = 2)
    private Double used;

    //Атмосферага тушадиган миқдори, т/йил
    @Column(name = "atmosphere_amount", precision = 20, scale = 2)
    private Double atmosphereAmount;

    @Column(name="deleted", columnDefinition = "boolean default false")
    private Boolean deleted;

}
