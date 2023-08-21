package com.example.twogether.board.dto;

import com.example.twogether.board.entity.Board;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BoardsResponseDto {

    private List<BoardResponseDto> boards;

    public static BoardsResponseDto of(List<Board> boards){ // 안 쓰이는 메서드... 나중에 다시 생각해보기
        List<BoardResponseDto> boardsResponseDto = boards.stream().map(BoardResponseDto::of).toList();
        return BoardsResponseDto.builder()
            .boards(boardsResponseDto)
            .build();
    }
}