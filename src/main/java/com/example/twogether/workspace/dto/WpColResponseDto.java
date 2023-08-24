package com.example.twogether.workspace.dto;


import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.entity.WorkspaceCollaborator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WpColResponseDto {

    private Long wpColId;
    private String email;
    private String nickname;

    public static WpColResponseDto of(WorkspaceCollaborator workspaceCollaborator) {
        return WpColResponseDto.builder()
            .wpColId(workspaceCollaborator.getId())
            .email(workspaceCollaborator.getUser().getEmail())
            .nickname(workspaceCollaborator.getUser().getNickname())
            .build();
    }
}
