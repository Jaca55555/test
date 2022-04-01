package uz.maroqand.ecology.core.repository.expertise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.maroqand.ecology.core.constant.expertise.CommentStatus;
import uz.maroqand.ecology.core.constant.expertise.CommentType;
import uz.maroqand.ecology.core.entity.expertise.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByRegApplicationIdAndTypeAndDeletedFalseOrderByIdDesc(Integer id, CommentType type);
    @Query("SELECT count(c) from  Comment  c where c.status=?1 and c.type=?2 and c.createdById<>?3 and c.deleted=false and c.regApplicationId=?4")
    Integer CountByStatusAndPerformerId(CommentStatus status,CommentType type , Integer performerId,Integer regApplicationId);
}
