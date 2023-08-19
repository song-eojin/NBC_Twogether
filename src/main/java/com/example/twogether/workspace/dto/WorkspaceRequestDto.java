package com.example.twogether.workspace.dto;

import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkspaceRequestDto {

    private String title;
    private String icon;

    public Workspace toEntity(User user) {
        return Workspace.builder()
            .title(this.title)
            .icon(this.icon)
            .user(user)
            .build();
    }
}
