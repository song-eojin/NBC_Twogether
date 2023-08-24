package com.example.twogether.workspace.dto;

import com.example.twogether.workspace.entity.WpColWp;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WpColWpResponseDto {
    private Long workspaceId;
    private Long workspaceCollaboratorId;
    private String workspaceTitle;

    public static WpColWpResponseDto of(WpColWp wpColWp) {
        return WpColWpResponseDto.builder()
            .workspaceId(wpColWp.getWorkspace().getId())
            .workspaceCollaboratorId(wpColWp.getWorkspaceCollaborator().getId())
            .workspaceTitle(wpColWp.getWorkspace().getTitle())
            .build();
    }
}
