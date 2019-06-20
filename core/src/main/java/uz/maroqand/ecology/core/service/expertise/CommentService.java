package uz.maroqand.ecology.core.service.expertise;

import uz.maroqand.ecology.core.entity.expertise.Comment;

public interface CommentService {
    Comment getByRegApplicationId(Integer id);
    Comment createComment(Comment comment);
    Comment updateComment(Comment comment);
}
