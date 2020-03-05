package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import uz.maroqand.ecology.core.entity.sys.File;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name = "library")
public class Library implements Serializable {
    @javax.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "nameru")
    private String nameRu;
    @Column(name = "namekr")
    private String nameKr;
    @Column(name = "ftext")
    private String ftext;
    @Column(name = "ftextru")
    private String ftextRu;
    @Column(name = "ftextkr")
    private String ftextKr;

    public String getFtext() {
        return ftext;
    }

    public void setFtext(String ftext) {
        this.ftext = ftext;
    }

    public String getFtextRu() {
        return ftextRu;
    }

    public void setFtextRu(String ftextRu) {
        this.ftextRu = ftextRu;
    }

    public String getFtextKr() {
        return ftextKr;
    }

    public void setFtextKr(String ftextKr) {
        this.ftextKr = ftextKr;
    }

    @Column(name = "number")
    private String number;
    @Column(name = "ldate")
    private Date ldate;
    @Column(name = "category_id")
    private Integer categoryId;
    @Column(name="created_by_id")
    private Integer created_by_id;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCreated_by_id() {
        return created_by_id;
    }

    public void setCreated_by_id(Integer created_by_id) {
        this.created_by_id = created_by_id;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "library_jt_content_files",
            joinColumns = { @JoinColumn(name = "appeal_id") },
            inverseJoinColumns = { @JoinColumn(name = "file_id") })
    private Set<File> contentFiles;

    public Set<File> getContentFiles() {
        return contentFiles;
    }

    public void setContentFiles(Set<File> contentFiles) {
        this.contentFiles = contentFiles;
    }

    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;
    @Column(name="created", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public String getNameKr() {
        return nameKr;
    }

    public void setNameKr(String nameKr) {
        this.nameKr = nameKr;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getLdate() {
        return ldate;
    }

    public void setLdate(Date ldate) {
        this.ldate = ldate;
    }



    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
