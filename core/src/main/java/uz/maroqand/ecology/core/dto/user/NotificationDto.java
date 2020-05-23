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
    private String url;
    private String title;
    private String applicationNumber;
    private String registrationNumber;
    private String message;
    private String createdAt;
    private String createdBy;

    public NotificationDto(Notification notification, HelperService helperService,String locale){
        this.id = notification.getId();
        this.url = notification.getUrl();
        this.title = helperService.getTranslation(notification.getTitle(),locale);
        this.applicationNumber = notification.getApplicationNumber()!=null?notification.getApplicationNumber().toString()+": " :"";
        this.registrationNumber = notification.getRegistrationNumber()!=null?notification.getRegistrationNumber()+": " :"";
        this.message = helperService.getTranslation(notification.getMessage(),locale);
        this.createdAt = notification.getCreatedAt()!=null? Common.uzbekistanDateAndTimeFormat.format(notification.getCreatedAt()):"";
        this.createdBy = helperService.getUserFullNameById(notification.getCreatedById());
    }
}
