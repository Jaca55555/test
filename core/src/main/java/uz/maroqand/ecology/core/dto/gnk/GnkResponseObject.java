package uz.maroqand.ecology.core.dto.gnk;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Utkirbek Boltaev on 17.07.2019.
 * (uz)
 * (ru)
 */
@Data
public class GnkResponseObject {

    private String err_code;
    private String err_text;
    private List<GnkRootResponseObject> root = new LinkedList<>();

}
