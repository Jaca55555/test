package uz.maroqand.ecology.core.dto.user;

import lombok.Data;
import uz.maroqand.ecology.core.entity.user.Notification;
import uz.maroqand.ecology.core.service.sys.impl.HelperService;
import uz.maroqand.ecology.core.util.Common;

/**
 * Created by Utkirbek Boltaev on 19.07.2019.
 * (uz)
 * (ru)
 */
@Data
public class NotificationDto {

    private Integer id;
    private String title;
    private String createdAt;
    private String createdBy;

    public NotificationDto(Notification notification, HelperService helperService){
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.createdAt = notification.getCreatedAt()!=null? Common.uzbekistanDateAndTimeFormat.format(notification.getCreatedAt()):"";
        this.createdBy = helperService.getUserFullNameById(notification.getCreatedById());
    }
}
