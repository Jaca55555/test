package uz.maroqand.ecology.core.dto.eimzo;


import uz.maroqand.ecology.core.constant.eimzo.YTErrorType;

import java.util.Date;

/**
 * Created by a.ruzmetov on 31.03.2017.
 */
public class VerifyPkcs7Dto {

    // respose olinganligi code
    private String responseCode="ERROR";
    // certificate ni publik key (eimzo egasini publik key i)
    private String certificatePublicKey;
    // certificate seria raqami (eimzo egasi)
    private String certificateSerialNumber;
    // certificate eimzo egaisini ismi
    private String certificateSubjectName;
    // certificate ochilgan vaqti (eimzo egasi)
    private Date certificateValidTo;
    // certificate tugash vaqti (eimzo egasi)
    private Date certificateValidFrom;
    // certificate ni active ligi (eimzo egasi)
    private Boolean verified;
    // certificate ni active ligi (eimzo egasi)
    private Boolean certificateVerified;
    // eimzo dan kelgan success filed value si
    private Boolean returnSuccess;
    // error bo`lsa eimzo dan qaytgan message
    private String errorReason;

    private Date signingTime;

    private YTErrorType errorType = YTErrorType.Unknown;


    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getCertificatePublicKey() {
        return certificatePublicKey;
    }

    public void setCertificatePublicKey(String certificatePublicKey) {
        this.certificatePublicKey = certificatePublicKey;
    }

    public String getCertificateSerialNumber() {
        return certificateSerialNumber;
    }

    public void setCertificateSerialNumber(String certificateSerialNumber) {
        this.certificateSerialNumber = certificateSerialNumber;
    }

    public Date getCertificateValidTo() {
        return certificateValidTo;
    }

    public void setCertificateValidTo(Date certificateValidTo) {
        this.certificateValidTo = certificateValidTo;
    }

    public Date getCertificateValidFrom() {
        return certificateValidFrom;
    }

    public void setCertificateValidFrom(Date certificateValidFrom) {
        this.certificateValidFrom = certificateValidFrom;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getCertificateVerified() {
        return certificateVerified;
    }

    public void setCertificateVerified(Boolean certificateVerified) {
        this.certificateVerified = certificateVerified;
    }

    public Boolean getReturnSuccess() {
        return returnSuccess;
    }

    public void setReturnSuccess(Boolean returnSuccess) {
        this.returnSuccess = returnSuccess;
    }

    public String getCertificateSubjectName() {
        return certificateSubjectName;
    }

    public void setCertificateSubjectName(String certificateSubjectName) {
        this.certificateSubjectName = certificateSubjectName;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public YTErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(YTErrorType errorType) {
        this.errorType = errorType;
    }

    @Override
    public String toString() {
        return "VerifyPkcs7Dto{" +
                "responseCode='" + responseCode + '\'' +
                ", certificatePublicKey='" + certificatePublicKey + '\'' +
                ", certificateSerialNumber='" + certificateSerialNumber + '\'' +
                ", certificateSubjectName='" + certificateSubjectName + '\'' +
                ", certificateValidTo='" + certificateValidTo + '\'' +
                ", certificateValidFrom='" + certificateValidFrom + '\'' +
                ", verified=" + verified +
                ", certificateVerified=" + certificateVerified +
                ", returnSuccess=" + returnSuccess +
                ", errorReason='" + errorReason + '\'' +
                ", errorType=" + errorType +
                '}';
    }

    public Date getSigningTime() {
        return signingTime;
    }

    public void setSigningTime(Date signingTime) {
        this.signingTime = signingTime;
    }
}
