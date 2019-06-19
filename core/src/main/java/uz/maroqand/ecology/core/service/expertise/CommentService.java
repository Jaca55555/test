package uz.maroqand.ecology.core.service.expertise;

import uz.maroqand.ecology.core.entity.expertise.Comment;

import java.util.List;

public interface CommentService {

    Comment getById(Integer id);

    Comment createComment(Comment comment);

    List<Comment> getListByRegApplicationId(Integer id);
}
