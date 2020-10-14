package uz.maroqand.ecology.core.constant.billing;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public enum InvoiceStatus {
    Initial, //В процессе, To'lanmagan
    Success, //ОПЛАЧЕН , To'langan
    Canceled, //ОТМЕНЕН, Bekor qilingan
    Error, //Не успешно, Xatolik bor
    PartialSuccess, //ОПЛАЧЕН , To'langan faqat qisman 15%
}