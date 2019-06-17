package uz.maroqand.ecology.core.service.expertise.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.maroqand.ecology.core.entity.expertise.Comment;
import uz.maroqand.ecology.core.repository.expertise.CommentRepository;
import uz.maroqand.ecology.core.service.expertise.CommentService;

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
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }
}
