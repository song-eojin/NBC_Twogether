package com.example.twogether.workspace.dto;

import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.entity.WorkspaceCollaborator;
import lombok.Getter;

@Getter
public class WpColRequestDto {

    private Long id; // 이거 있어야 하는지 고민 중
    private String email;

    public static WorkspaceCollaborator toEntity(User user, Workspace workspace) {
        return WorkspaceCollaborator.builder()
            .id(user.getId())
            .email(user.getEmail())
            .user(user)
            .workspace(workspace)
            .build();
    }
}
