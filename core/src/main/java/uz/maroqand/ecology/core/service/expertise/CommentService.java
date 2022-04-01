package uz.maroqand.ecology.core.service.expertise;

import uz.maroqand.ecology.core.constant.expertise.CommentStatus;
import uz.maroqand.ecology.core.constant.expertise.CommentType;
import uz.maroqand.ecology.core.entity.expertise.Comment;

import java.util.List;

public interface CommentService {

    Comment getById(Integer id);

    Comment create(Integer regApplicationId, CommentType type, String message, Integer createdById);

    Comment updateComment(Comment comment);

    List<Comment> getByRegApplicationIdAndType(Integer id, CommentType type);
    Integer CountByStatusAndPerformerId(CommentStatus status, CommentType type , Integer PerformerId, Integer regApplicationId);

}
