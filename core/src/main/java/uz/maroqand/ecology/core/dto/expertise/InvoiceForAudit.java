package uz.maroqand.ecology.core.dto.expertise;


import uz.maroqand.ecology.core.constant.billing.InvoiceStatus;
import uz.maroqand.ecology.core.entity.billing.Invoice;
import uz.maroqand.ecology.core.entity.client.Client;
import uz.maroqand.ecology.core.entity.sys.Organization;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Akmal Sadullayev on 04.05.2020.
 * (uz)
 * (ru)
 */
public class InvoiceForAudit {

    private Integer id;
    private Integer clientId;
    private String payerName;
    private Integer payeeId;
    private String payeeName;
    private String payeeAccount;
    private Integer payeeTin;
    private String payeeAddress;
    private String payeeMfo;
    private String invoice;
    private Double amount;
    private Double qty;
    private Date createdDate;
    private Date expireDate;
    private Date closedDate;
    private Date canceledDate;
    private String detail;
    private InvoiceStatus status;
    private Boolean deleted = false;
    private Date registeredAt;
    private Date updatedAt;

    public InvoiceForAudit(Invoice invoice) {
        this.id = invoice.getId();
        this.clientId = invoice.getClientId();
        this.payerName = invoice.getPayerName();
        this.payeeId = invoice.getPayeeId();
        this.payeeName = invoice.getPayeeName();
        this.payeeAccount = invoice.getPayeeAccount();
        this.payeeTin = invoice.getPayeeTin();
        this.payeeAddress = invoice.getPayeeAddress();
        this.payeeMfo = invoice.getPayeeMfo();
        this.invoice = invoice.getInvoice();
        this.amount = invoice.getAmount();
        this.qty = invoice.getQty();
        this.expireDate = invoice.getExpireDate();
        this.detail = invoice.getDetail();
        this.status = invoice.getStatus();
        this.deleted = invoice.getDeleted();
        this.updatedAt = invoice.getUpdatedAt();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public Integer getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Integer payeeId) {
        this.payeeId = payeeId;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }

    public Integer getPayeeTin() {
        return payeeTin;
    }

    public void setPayeeTin(Integer payeeTin) {
        this.payeeTin = payeeTin;
    }

    public String getPayeeAddress() {
        return payeeAddress;
    }

    public void setPayeeAddress(String payeeAddress) {
        this.payeeAddress = payeeAddress;
    }

    public String getPayeeMfo() {
        return payeeMfo;
    }

    public void setPayeeMfo(String payeeMfo) {
        this.payeeMfo = payeeMfo;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public Date getCanceledDate() {
        return canceledDate;
    }

    public void setCanceledDate(Date canceledDate) {
        this.canceledDate = canceledDate;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
