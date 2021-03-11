package uz.maroqand.ecology.cabinet.constant.billing;

/**
 * Created by Utkirbek Boltaev on 23.07.2019.
 * (uz)
 * (ru)
 */
public class BillingUrls {
    private static final String Prefix = "/billing";


    private static final String PaymentFile = Prefix + "/payment_file";
    public static final String PaymentFileList = PaymentFile + "/list";
    public static final String PaymentFileListAjax = PaymentFile + "/ajax";
    public static final String PaymentFileView = PaymentFile + "/view";
    public static final String PaymentFileEdit = PaymentFile + "/edit";
    public static final String PaymentFileEditSubmit = PaymentFile + "/edit_submit";

    // hamma to'lovlarni ko'rish uchun
    private static final String PaymentFileAll = Prefix + "/payment_file_all";
    public static final String PaymentFileAllList = PaymentFileAll + "/list";
    public static final String PaymentFileAllListAjax = PaymentFileAll + "/ajax";
    public static final String PaymentFileAllDelete = PaymentFileAll + "/delete";
    public static final String PaymentFileAllView = PaymentFileAll + "/view";
    public static final String PaymentFilIsRemoveInvoiceView = PaymentFileAll + "/is_remove_invoice";
    public static final String PaymentFileAllEdit = PaymentFileAll + "/edit";
    public static final String PaymentFileAllEditSubmit = PaymentFileAll + "/edit_submit";

    // xato kiritilgan bank malumotlar kelganda arizadagi invoice bilan ulash ushun qo'shildi
    public static final String PaymentFileAllConnectInvoice = PaymentFileAll + "/connect_invoice";
    public static final String PaymentFileAllGetInvoice = PaymentFileAll + "/get_invoice";
    public static final String PaymentFileAllConnectInvoiceSubmit = PaymentFileAll + "/connect_invoice_submit";
    public static final String PaymentFileConnectInvoice = PaymentFile + "/connect_invoice";
    public static final String PaymentFileGetInvoice = PaymentFile + "/get_invoice";
    public static final String PaymentFileConnectInvoiceSubmit = PaymentFile + "/connect_invoice_submit";


}
