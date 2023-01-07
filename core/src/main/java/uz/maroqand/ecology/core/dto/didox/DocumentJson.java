package uz.maroqand.ecology.core.dto.didox;

import lombok.Data;

import java.util.LinkedList;

@Data
public class DocumentJson{
    public String didoxcontractid;
    public int version;
    public LinkedList<Object> waybillids;
    public boolean hasmarking;
    public int facturatype;
    public Productlist productlist;
    public Facturadoc facturadoc;
    public Contractdoc contractdoc;
    public Object contractid;
    public String lotid;
    public Oldfacturadoc oldfacturadoc;
    public String sellertin;
    public Seller seller;
    public Itemreleaseddoc itemreleaseddoc;
    public String buyertin;
    public Buyer buyer;
    public Facturaempowermentdoc facturaempowermentdoc;
    public Expansion expansion;
    public Foreigncompany foreigncompany;
    public String facturaid;
}