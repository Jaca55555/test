package uz.maroqand.ecology.core.entity.sys;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "event_news")
public class EventNews {

    @Transient
    private static final String sequenceName = "event_news_id_seq";

    @Id
    @SequenceGenerator(name = sequenceName, sequenceName = sequenceName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = sequenceName)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "theme")
    private String theme;

    @Column(name = "description", length = 10000)
    private String description;

    @Column(name = "file_id")
    private Integer fileId;

    @OneToOne()
    @JoinColumn(name = "file_id", insertable = false, updatable = false)
    private File file;

    @Column(name = "status")
    private boolean status;

    @Column(name = "delete")
    private boolean delete =  false;

    @Column(name="created_at", columnDefinition = "timestamp without time zone")
    private Date createdAt;

    @Column(name="update_at", columnDefinition = "timestamp without time zone")
    private Date updateAt;

    @Column(name = "created_by")
    private Integer createdById;

    @Column(name = "update_by")
    private Integer updateById;








}
