package uz.maroqand.ecology.cabinet.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.config.ScheduledTask;
import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.constant.telegram.SendQueryType;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.expertise.RegApplication;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.billing.InvoiceService;
import uz.maroqand.ecology.core.service.expertise.RegApplicationService;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.service.Bot;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentSubService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 21.06.2018.
 * (uz)
 * (ru)
 */
public class Maintenance {

    private static Logger logger = LogManager.getLogger(ScheduledTask.class);

    public static void removeInvoiceAndRemoveApplication(InvoiceService invoiceService, RegApplicationService regApplicationService){

        List<Invoice> invoiceList = invoiceService.getListByStatus(InvoiceStatus.Initial);

        for (Invoice invoice:invoiceList) {
            if (invoice.getCreatedDate()!=null){
                Date createdDate = invoice.getCreatedDate();
                Calendar c = Calendar.getInstance();
                Date date = new Date();
                c.setTime(date);
                c.add(Calendar.DATE,-31);    // shu kunning o'zi ham qo'shildi
                Date expireDate = c.getTime();
//                Invoice yaratilganiga 90 kundan oshgan
                if (createdDate.before(expireDate)){
                    invoiceService.cancelInvoice(invoice);
                    regApplicationService.cancelApplicationByInvoiceId(invoice.getId());;
                }
            }
        }
    }


    public static void sendAllDocumentCount(UserService userService, DocumentTaskSubService documentTaskSubService, Bot bot) {
        List<User> userList = userService.getAllByTelegramUsers();
        for (User user:userList) {
            bot.sendMsg(user.getTelegramUserId(),documentTaskSubService.getMessageText(user.getTelegramUserId(), SendQueryType.NewDocument));
        }
    }
        public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        c.add(Calendar.DAY_OF_MONTH, 3);
        Date date = c.getTime();
        c.add(Calendar.DAY_OF_MONTH, 1);
        Date date1 = c.getTime();

        System.out.println(date);
        System.out.println(date1);
    }
}
