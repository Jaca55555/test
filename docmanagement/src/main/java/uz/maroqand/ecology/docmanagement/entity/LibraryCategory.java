package uz.maroqand.ecology.docmanagement.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="library_category")
public class LibraryCategory {
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

    @Column(name = "count")
    private Integer count;
    @Column(name = "level")
    private Integer level;
    @Column(name = "parent")
    private Integer parent;
    @Column(name = "deleted",columnDefinition = "boolean DEFAULT false")
    private Boolean deleted = false;
    @Column(name="created", columnDefinition = "timestamp without time zone")
    private Date createdAt;
    public Integer getCreated_by_id() {
        return created_by_id;
    }

    public void setCreated_by_id(Integer created_by_id) {
        this.created_by_id = created_by_id;
    }

    @Column(name="created_by_id")
    private Integer created_by_id;
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

}
