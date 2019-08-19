package uz.maroqand.ecology.core.dto.sms;

/**
 * Created by Utkirbek Boltaev on 17.04.2019.
 * (uz)
 * (ru)
 */
public class ContactDto {

    private String name; //FIO
    private ContactNumberDto number; //{"msisdn": "998937438468"}
    private String tag; // XXX
    private String gender; //M , F
    private String occupation; //Hunarmaqnd
    private Integer age; //30
    private String note; //Tuman

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContactNumberDto getNumber() {
        return number;
    }

    public void setNumber(ContactNumberDto number) {
        this.number = number;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
