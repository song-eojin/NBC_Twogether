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
public class WpsResponseDto {

    private List<WpResponseDto> workspaces;

    public static WpsResponseDto of(List<Workspace> workspaces) {

        List<WpResponseDto> workspacesResponseDto = workspaces.stream()
            .map(WpResponseDto::of)
            .toList();

        return WpsResponseDto.builder()
            .workspaces(workspacesResponseDto)
            .build();
    }
}
