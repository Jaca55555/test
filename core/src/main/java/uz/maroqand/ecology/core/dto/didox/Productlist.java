package uz.maroqand.ecology.core.dto.didox;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;

@Data
public class Productlist{
    public LinkedList<Product> products;
    public boolean hascommittent;
    public boolean haslgota;
    public String tin;
    public boolean hasexcise;
    public boolean hasvat;
    public String facturaproductid;


}