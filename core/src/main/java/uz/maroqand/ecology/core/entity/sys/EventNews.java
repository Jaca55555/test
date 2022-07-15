package uz.maroqand.ecology.core.entity.sys;

import lombok.Data;

import javax.persistence.*;

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

    





}
