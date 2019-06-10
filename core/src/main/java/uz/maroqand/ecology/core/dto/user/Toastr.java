package uz.maroqand.ecology.core.dto.user;

import lombok.Data;
import uz.maroqand.ecology.core.constant.user.ToastrType;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

/**
 * Created by Utkirbek Boltaev on 03.06.2019.
 * (uz)
 * (ru)
 */
@Data
public class Toastr {

    private Integer userId;

    @Enumerated(EnumType.ORDINAL)
    private ToastrType toastrType;

    @Column
    private String title;

    @Column
    private String message;

    @Column(name = "date", columnDefinition = "timestamp without time zone")
    private Date date;

}