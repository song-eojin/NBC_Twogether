package com.example.twogether.workspace.dto;

import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.entity.WorkspaceCollaborator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WpColRequestDto {
    private String email;

    public static WorkspaceCollaborator toEntity(User user, Workspace workspace) {
        Long newId = WorkspaceCollaborator.generateUniqueId();
        return WorkspaceCollaborator.builder()
            .id(newId)
            .email(user.getEmail())
            .user(user)
            .workspace(workspace)
            .build();
    }
}
