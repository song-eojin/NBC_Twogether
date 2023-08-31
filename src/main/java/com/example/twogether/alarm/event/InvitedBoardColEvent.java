package com.example.twogether.alarm.event;

import com.example.twogether.board.entity.Board;
import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InvitedBoardColEvent extends ApplicationEvent {

    private final User invitingUser;
    private final User invitedUser;
    private final Board board;
    private final String content;

    @Builder
    public InvitedBoardColEvent(Object source, User invitingUser, User invitedUser, Board board) {
        super(source);
        this.invitingUser = invitingUser;
        this.invitedUser = invitedUser;
        this.board = board;
        this.content = generateContent(invitingUser, invitedUser, board);
    }

    private String generateContent(User invitingUser, User invitedUser, Board board) {

        return "보드 오너(" + invitingUser.getNickname() + ")가 "
            + "당신(" + invitedUser.getNickname() + ")을 "
            + "\'ID" + board.getWorkspace().getId() + ". " + board.getWorkspace().getTitle() + "\' 워크스페이스에 포함되어 있는 "
            + "\'ID" + board.getId() + ". " + board.getTitle() + "\' 보드에 초대했습니다.";
    }
}
