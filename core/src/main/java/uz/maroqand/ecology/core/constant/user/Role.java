package uz.maroqand.ecology.core.constant.user;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 * (ru)
 */
public enum Role {

    ADMIN,
    REG_APPEAL, //Murojaat ro'yhatdan o'tkazish (Call center uchun)
    REGISTER_APPEAL, //Murojaat ro'yhatdan o'tkazish va task qo'shish (DXM qabulxona)
    APPEAL_TASKS_LIST, //Murojaatlar va tasklarni ko'rish (Bosh vazir devonxona, DXM qabulxona va Tashkilotlar uchun)
    APPEAL_TASKS_NEW, //Murojaatga task qo'shish (Bosh vazir devonxona VA DXM qabulxona)
    APPEAL_TASKS_PROCESS, //Task jarayonlari (Tashkilotlar uchun)
    APPEAL_TASKS_CLOSED, //Murojaatga task qo'shish (Bosh vazir devonxona)
    APPLICANT, //Murojaatchilar bazasi

}