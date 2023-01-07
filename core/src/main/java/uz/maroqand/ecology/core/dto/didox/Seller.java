package uz.maroqand.ecology.core.dto.didox;

import lombok.Data;

@Data
public class Seller{
    public String name;
    public String vatregcode;
    public String account;
    public String bankid;
    public String address;
    public String director;
    public String accountant;
    public Integer vatregstatus;
    public String taxgap;
}