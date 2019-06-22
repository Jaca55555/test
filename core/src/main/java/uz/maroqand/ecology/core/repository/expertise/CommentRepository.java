package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.maroqand.ecology.core.entity.expertise.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Comment getByRegApplicationId(Integer id);

    List<Comment> findByRegApplicationIdAndDeletedFalse(Integer id);
}
