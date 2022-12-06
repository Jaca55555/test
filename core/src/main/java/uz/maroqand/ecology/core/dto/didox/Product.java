package uz.maroqand.ecology.core.dto.didox;

import lombok.Data;

@Data
public class Product{
    public Expansion expansion;
    public int ordno;
    public String lgotaid;
    public String committentname;
    public String committenttin;
    public String committentvatregcode;
    public String name;
    public String catalogcode;
    public String catalogname;
    public String barcode;
    public String measureid;
    public int count;
    public String summa;
    public String deliverysum;
    public int vatrate;
    public int vatsum;
    public int exciserate;
    public int excisesum;
    public String deliverysumwithvat;
    public boolean withoutvat;
}