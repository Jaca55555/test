package uz.maroqand.ecology.core.dto.didox_response;

import java.util.ArrayList;
import java.util.LinkedList;

public class Document{
    public String name;
    public String doctype;
    public String updated;
    public int status;
    public int doc_status;
    public String status_comment;
    public int owner;
    public String parties_company;
    public String parties_first_name;
    public String parties_last_name;
    public String parties_phone;
    public String parties_email;
    public String _id;
    public String doc_id;
    public int total_sum;
    public int total_delivery_sum;
    public int total_vat_sum;
    public int total_delivery_sum_with_vat;
    public String type;
    public ArrayList<TargetTin> target_tins;
    public boolean document_json;
    public boolean has_vat;

}
