package com.example.twogether.comment.repository;

import com.example.twogether.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    void deleteAllByCard_Id(Long id);
}
