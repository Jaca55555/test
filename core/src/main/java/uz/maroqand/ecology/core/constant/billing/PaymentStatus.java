package uz.maroqand.ecology.core.constant.billing;

/**
 * Created by Utkirbek Boltaev on 15.06.2019.
 * (uz)
 * (ru)
 */
public enum PaymentStatus {
    Initial,
    CheckCard,
    SendSms,
    SendSmsError,
    Success,
    Error,
    AlreadyPaid
}
