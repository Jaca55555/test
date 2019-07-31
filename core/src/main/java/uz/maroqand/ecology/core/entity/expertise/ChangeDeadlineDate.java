package uz.maroqand.ecology.core.entity.expertise;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.constant.expertise.ChangeDeadlineDateStatus;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "change_deadline_date")
public class ChangeDeadlineDate {

    @Transient
    private static final String sequenceName = "change_deadline_date_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reg_application_id", insertable = false, updatable = false)
    private RegApplication regApplication;

    @Column(name = "reg_application_id")
    private Integer regApplicationId;

    private String reason;

    @Column(name = "after_deadline_date")
    private Date afterDeadlineDate;

    @Column(name = "before_deadline_date")
    private Date beforeDeadlineDate;

    @Column(name = "deadline_date_status")
    @Enumerated(EnumType.ORDINAL)
    private ChangeDeadlineDateStatus status;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User createdBy;

    @Column(name = "created_by")
    private Integer createdById;


}
