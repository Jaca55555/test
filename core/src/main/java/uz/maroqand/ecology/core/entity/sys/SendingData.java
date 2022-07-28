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

    @Column()
    private String dataSend;

    @Column
    private Date createdAt;

    @Column
    private short dativeryStatus;

    @Column
    private String errors;

    @Column
    private Integer regApplicationId;


}
