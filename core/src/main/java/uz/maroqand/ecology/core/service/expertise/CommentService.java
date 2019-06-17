package uz.maroqand.ecology.core.service.expertise;

import uz.maroqand.ecology.core.entity.expertise.Comment;

public interface CommentService {
    Comment getById(Integer id);
    Comment createComment(Comment comment);
}
