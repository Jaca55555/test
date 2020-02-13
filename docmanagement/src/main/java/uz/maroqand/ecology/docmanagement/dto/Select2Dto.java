package uz.maroqand.ecology.docmanagement.dto;

/**
 * Created by Utkirbek Boltaev on 13.05.2019.
 * (uz)
 * (ru)
 */
public class Select2Dto {

    private Integer id;
    private String text;

    public Select2Dto(Integer id, String text){
        this.id = id;
        this.text = text;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
