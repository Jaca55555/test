package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.constant.expertise.CommentStatus;
import uz.maroqand.ecology.core.constant.expertise.CommentType;
import uz.maroqand.ecology.core.entity.expertise.Comment;
import uz.maroqand.ecology.core.repository.expertise.CommentRepository;
import uz.maroqand.ecology.core.service.expertise.CommentService;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment getById(Integer id) {
        return commentRepository.getOne(id);
    }

    @Override
    public Comment create(Integer regApplicationId, CommentType type, String message, Integer createdById) {
        Comment comment = new Comment();
        comment.setRegApplicationId(regApplicationId);
        comment.setType(type);
        comment.setMessage(message);
        comment.setStatus(CommentStatus.New);
        comment.setCreatedById(createdById);
        comment.setCreatedAt(new Date());
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getByRegApplicationIdAndType(Integer id, CommentType type) {
        return commentRepository.findByRegApplicationIdAndTypeAndDeletedFalseOrderByIdDesc(id, type);
    }

    @Override
    public Integer CountByStatusAndPerformerId(CommentStatus status, CommentType type, Integer performerId, Integer regApplicationId) {
        return commentRepository.CountByStatusAndPerformerId(status,type, performerId,regApplicationId);
    }
}
