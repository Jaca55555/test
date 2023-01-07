package uz.maroqand.ecology.core.dto.didox;

import lombok.Data;

@Data
public class Product{
    public String id;
    public Integer ordno;
    public String lgotaid;
    public String committentname;
    public String committenttin;
    public String committentvatregcode;
    public String committentvatregstatus;
    public String name;
    public String catalogcode;
    public String catalogname;
    public Object marks;
    public String barcode;
    public Object measureid;
    public String packagecode;
    public String packagename;
    public String count;
    public String summa;
    public String deliverysum;
    public String vatrate;
    public String vatsum;
    public int exciserate;
    public int excisesum;
    public String deliverysumwithvat;
    public boolean withoutvat;
    public boolean withoutexcise;
    public String lgotaname;
    public Integer lgotavatsum;
    public Integer lgotatype;
}