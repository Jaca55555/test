package uz.maroqand.ecology.core.service.sys.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.sys.EventNews;
import uz.maroqand.ecology.core.repository.sys.EventNewsRepository;
import uz.maroqand.ecology.core.service.sys.EventNewsService;

import java.util.List;

@Service
public class EventNewsServiceImpl implements EventNewsService {

    private final EventNewsRepository eventNewsRepository;

    public EventNewsServiceImpl(EventNewsRepository eventNewsRepository) {
        this.eventNewsRepository = eventNewsRepository;
    }

    @Override
    public EventNews getById(Integer id, Integer createById) {
        return eventNewsRepository.findByIdAndCreatedById(id, createById);
    }

    @Override
    public void save(EventNews eventNews) {
        eventNewsRepository.save(eventNews);
    }

    @Override
    public void update(EventNews eventNews) {
        eventNewsRepository.save(eventNews);
    }

    @Override
    public Page<EventNews> getAllEvent(Pageable pageable) {
        return eventNewsRepository.findAllByDeleteIsFalse(pageable);
    }

    @Override
    public List<EventNews> getNewsByDate() {
        List<EventNews> page =eventNewsRepository.findAllByDeleteIsFalseAndStatusIsTrueOrderByIdAscLimit();
        return page;
    }

    @Override
    public EventNews findById(Integer id) {
        return eventNewsRepository.findById(id).orElse(null);
    }
}
