package uz.maroqand.ecology.core.entity.expertise;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Utkirbek Boltaev on 27.01.2010.
 * (uz)
 */
@Getter
@Setter
@Entity
@Table(name = "facture_product")
public class FactureProduct {

    @Transient
    private static final String sequenceName = "facture_product_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "facture_id")
    private Integer factureId;

    //№
    @Column(name = "number")
    private Integer number;

    //Наименование товаров (работ, услуг)
    @Column(name = "name")
    private String name;

    //Ед.
    @Column(name = "unit")
    private String unit;

    //Кол-во
    @Column(name = "qty")
    private Integer qty;

    //Цена
    @Column(name = "price")
    private Double price;

    //Стоимость поставки
    @Column(name = "cost")
    private Double cost;

    //НДС Став-ка
    @Column(name = "vat_present")
    private Double vatPresent;

    //НДС Сумма
    @Column(name = "vat_sum")
    private Double vatSum;

    //Стоимость поставки с учетом НДС
    @Column(name = "total")
    private Double total;

}
