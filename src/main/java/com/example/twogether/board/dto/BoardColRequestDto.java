package com.example.twogether.board.dto;

import com.example.twogether.board.entity.Board;
import com.example.twogether.board.entity.BoardCollaborator;
import com.example.twogether.user.entity.User;
import lombok.Getter;

@Getter
public class BoardColRequestDto {

    private String email;

    public static BoardCollaborator toEntity(User user, Board board) {
        return BoardCollaborator.builder()
            .email(user.getEmail())
            .user(user)
            .board(board)
            .build();
    }
}
