package uz.maroqand.ecology.core.entity.sys;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "sending_data")
public class SendingData {

    @Transient
    private static final String sequenceName = "sending_data_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(length = 10000, name = "data_send")
    private String dataSend;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "delivery_status")
    private short deliveryStatus;

    @Column(length = 10000)
    private String errors;

    @Column
    private Integer regApplicationId;


}
