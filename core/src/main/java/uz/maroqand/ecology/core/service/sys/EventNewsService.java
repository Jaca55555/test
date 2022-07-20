package uz.maroqand.ecology.core.service.sys;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.maroqand.ecology.core.entity.sys.EventNews;

import java.util.List;

public interface EventNewsService {
    EventNews getById(Integer id, Integer id1);

    EventNews findById(Integer id);

    void save(EventNews eventNews);

    void update(EventNews eventNews);

    Page<EventNews> getAllEvent(Pageable pageable);

    List<EventNews> getNewsByDate();
}
