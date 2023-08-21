package com.example.twogether.board.dto;

import com.example.twogether.board.entity.Board;
import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.workspace.dto.WpColResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class BoardResponseDto {
    private Long boardId;
    private String nickname;
    private String title;
    private String color;
    private String info;
    private List<BoardColResponseDto> boardCollaborators;

    public static BoardResponseDto of(Board board) {
        return BoardResponseDto.builder()
            .boardId(board.getId())
            .nickname(board.getUser().getNickname())
            .title(board.getTitle())
            .color(board.getColor())
            .info(board.getInfo())
            .boardCollaborators(board.getBoardCollaborators().stream().map(
                BoardColResponseDto::of).toList())
            .build();
    }
}
