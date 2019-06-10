package uz.maroqand.ecology.core.constant.sys;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 * (ru)
 */
public enum SmsSendStatus {
    SCHEDLD,//запланировано, keyinchalik yuboriladi
    ENROUTE,//в пути,        yuborildi
    DELIVRD,//доставлено,    qabul qilindi
    EXPIRED,//просрочено,    muddati tugadi
    DELETED,//удалено       o`chilgan
    UNDELIV,//не доставлено, qabul qilinmadi
    ACCEPTD,//принято,       qabul qilingan
    UNKNOWN,//неизвестно,    noma'lum
    REJECTD,//отменено,      bekoq qilingan
}
