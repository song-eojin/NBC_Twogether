package com.example.twogether.comment.dto;

import com.example.twogether.card.entity.Card;
import com.example.twogether.comment.entity.Comment;
import com.example.twogether.user.entity.User;
import lombok.Getter;

@Getter
public class CommentRequestDto {

    private String content;

    public Comment toEntity(User user, Card card) {
        return Comment.builder()
            .content(content)
            .user(user)
            .card(card)
            .build();
    }
}
