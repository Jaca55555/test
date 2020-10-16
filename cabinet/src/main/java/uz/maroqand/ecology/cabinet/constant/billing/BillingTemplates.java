package uz.maroqand.ecology.cabinet.constant.billing;

/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */
public class BillingTemplates {
    private static final String Prefix = "billing";

    private static final String PaymentFile = Prefix + "/payment_file";
    public static final String PaymentFileList = PaymentFile + "/list";
    public static final String PaymentFileView = PaymentFile + "/view";
    public static final String PaymentFileEdit = PaymentFile + "/edit";

    //All Hamma to'lovlar uchun
    private static final String PaymentFileAll = Prefix + "/payment_file_all";
    public static final String PaymentFileAllList = PaymentFileAll + "/list";
    public static final String PaymentFileAllView = PaymentFileAll + "/view";
    public static final String PaymentFileAllEdit = PaymentFileAll + "/edit";

    // xato kiritilgan bank malumotlar kelganda arizadagi invoice bilan ulash ushun qo'shildi
    public static final String PaymentFileAllConnectInvoice = PaymentFileAll + "/connect_invoice";

}