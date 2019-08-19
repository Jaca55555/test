package uz.maroqand.ecology.core.dto.sms;

/**
 * Created by Utkirbek Boltaev on 11.04.2019.
 * (uz)
 * (ru)
 */
public class SmsSendDto {

    private String destAddr;
    private String text;

    public String getDestAddr() {
        return destAddr;
    }

    public void setDestAddr(String destAddr) {
        this.destAddr = destAddr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
