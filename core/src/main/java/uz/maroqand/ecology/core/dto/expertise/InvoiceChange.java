package uz.maroqand.ecology.core.dto.expertise;

import uz.maroqand.ecology.core.entity.billing.Invoice;

/**
 * Created by Sadullayev Akmal on 04.05.2020.
 * (uz)
 * (ru)
 */
public class InvoiceChange {

    private InvoiceForAudit _before = null;

    private InvoiceForAudit _after = null;

    private transient Invoice before = null;

    private transient Invoice after = null;

    public InvoiceForAudit get_before() {
        return _before;
    }

    public void set_before(InvoiceForAudit _before) {
        this._before = _before;
    }

    public InvoiceForAudit get_after() {
        return _after;
    }

    public void set_after(InvoiceForAudit _after) {
        this._after = _after;
    }

    public Invoice getBefore() {
        return before;
    }

    public void setBefore(Invoice before) {
        if (before != null) {
            this.before = before;
            this._before = new InvoiceForAudit(before);
        }
    }

    public Invoice getAfter() {
        return after;
    }

    public void setAfter(Invoice after) {
        if (after != null) {
            this.after = after;
            this._after = new InvoiceForAudit(after);
        }
    }
}
