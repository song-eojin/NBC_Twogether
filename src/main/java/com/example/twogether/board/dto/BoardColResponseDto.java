package com.example.twogether.board.dto;

import com.example.twogether.board.entity.BoardCollaborator;
import com.example.twogether.workspace.dto.WpColResponseDto;
import com.example.twogether.workspace.entity.WorkspaceCollaborator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardColResponseDto {

    private Long boardColId;
    private String email;
    private String nickname;

    public static BoardColResponseDto of(BoardCollaborator boardCollaborator) {
        return BoardColResponseDto.builder()
            .boardColId(boardCollaborator.getId())
            .email(boardCollaborator.getUser().getEmail())
            .nickname(boardCollaborator.getUser().getNickname())
            .build();
    }
}
