package com.example.twogether.workspace.dto;

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
public class WorkspacesResponseDto {

    private List<WorkspaceResponseDto> workspaces;

    public static WorkspacesResponseDto of(List<Workspace> workspaces) {
        List<WorkspaceResponseDto> workspacesResponseDto = workspaces.stream()
            .map(WorkspaceResponseDto::of)
            .toList();
        return WorkspacesResponseDto.builder()
            .workspaces(workspacesResponseDto)
            .build();
    }
}
