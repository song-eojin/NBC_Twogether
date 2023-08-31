package com.example.twogether.board.dto;

import com.example.twogether.board.entity.Board;
import com.example.twogether.deck.dto.DeckResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BoardResponseDto {

    private Long boardId;
    private String email;
    private String nickname;
    private String title;
    private String color;
    private String info;

    private List<BoardColResponseDto> boardCollaborators;
    private List<DeckResponseDto> decks;

    public static BoardResponseDto of(Board board) {
        return BoardResponseDto.builder()
            .boardId(board.getId())
            .email(board.getUser().getEmail())
            .nickname(board.getUser().getNickname())
            .title(board.getTitle())
            .color(board.getColor())
            .info(board.getInfo())
            .boardCollaborators(board.getBoardCollaborators().stream().map(
                BoardColResponseDto::of).toList())
            .decks(board.getDecks().stream().map(DeckResponseDto::new).toList())
            .build();
    }
}
