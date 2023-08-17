package com.example.twogether.board.dto;

import com.example.twogether.board.entity.Board;
import com.example.twogether.board.entity.BoardUser;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardUserRequestDto {
    private Long userId;

//    public static BoardUser toEntity(User collaborator, Board board) {
//        return BoardUser.builder()
//            .id(collaborator.getId())
//            .collaborator(collaborator)
//            .board(board)
//            .build();
//    }
}
