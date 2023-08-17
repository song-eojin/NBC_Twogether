package com.example.twogether.board.dto;

import com.example.twogether.board.entity.BoardUser;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardUsersResponseDto {
    private List<BoardUserResponseDto> collaborators;

    public static BoardUsersResponseDto of(List<BoardUser> boardUsers) {
        List<BoardUserResponseDto> boardUsersResponseDto = boardUsers.stream()
            .map(BoardUserResponseDto::of)
            .toList();
        return BoardUsersResponseDto.builder()
            .collaborators(boardUsersResponseDto)
            .build();
    }

}