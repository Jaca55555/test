package uz.maroqand.ecology.core.repository.sys;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.maroqand.ecology.core.entity.sys.EventNews;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface EventNewsRepository extends JpaRepository<EventNews, Integer> {

    Page<EventNews> findAllByDeleteIsFalse(Pageable pageable);

    EventNews findByIdAndCreatedById(Integer id, Integer cretedById);

    @Query(value = "SELECT * FROM event_news en where en.delete is false and en.status is true order by en.id desc limit 6", nativeQuery = true)
    List<EventNews> findAllByDeleteIsFalseAndStatusIsTrueOrderByIdAscLimit();

}
