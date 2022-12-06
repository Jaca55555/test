package uz.maroqand.ecology.core.dto.didox;

import lombok.Data;

import java.util.ArrayList;
@Data
public class Productlist{
    public boolean hascommittent;
    public boolean haslgota;
    public String tin;
    public boolean hasexcise;
    public boolean hasvat;
    public ArrayList<Product> products;
    public String facturaproductid;
}