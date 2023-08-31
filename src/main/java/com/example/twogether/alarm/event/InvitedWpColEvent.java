package com.example.twogether.alarm.event;

import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InvitedWpColEvent extends ApplicationEvent {

    private final User invitingUser;
    private final User invitedUser;
    private final Workspace workspace;
    private final String content;

    @Builder
    public InvitedWpColEvent(Object source, User invitingUser, User invitedUser, Workspace workspace) {
        super(source);
        this.invitingUser = invitingUser;
        this.invitedUser = invitedUser;
        this.workspace = workspace;
        this.content = generateContent(invitingUser, invitedUser, workspace);
    }

    private String generateContent(User invitingUser, User invitedUser, Workspace workspace) {

        return "워크스페이스 오너(" + invitingUser.getNickname() + ")가 "
            + "당신(" + invitedUser.getNickname() + ")을 "
            + "\'ID" + workspace.getId() + ". " + workspace.getTitle() + "\' 워크스페이스에 초대했습니다.";
    }
}
