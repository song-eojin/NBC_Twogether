package com.example.twogether.workspace.dto;

import com.example.twogether.board.dto.BoardResponseDto;
import com.example.twogether.workspace.entity.Workspace;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WpResponseDto {
    private Long workspaceId;
    private String title;
    private String icon;
    private String email;
    private List<WpColResponseDto> wpCollaborators;
    private List<BoardResponseDto> boards;


    public static WpResponseDto of(Workspace workspace) {
        return WpResponseDto.builder()
            .workspaceId(workspace.getId())
            .email(workspace.getUser().getEmail())
            .title(workspace.getTitle())
            .icon(workspace.getIcon())
            .wpCollaborators(workspace.getWorkspaceCollaborators().stream().map(WpColResponseDto::of).toList())
            .boards(workspace.getBoards().stream().map(BoardResponseDto::of).toList())
            .build();
    }
}
