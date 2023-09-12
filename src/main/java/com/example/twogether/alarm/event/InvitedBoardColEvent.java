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
    private final String title;
    private final String content;

    @Builder
    public InvitedBoardColEvent(Object source, User editor, User invitedUser, Board board) {
        super(source);
        this.invitingUser = editor;
        this.invitedUser = invitedUser;
        this.board = board;
        this.title = "Invited to thd Board";
        this.content = generateContent(board);
    }

    private String generateContent(Board board) {

        return "Workspace Title : " + board.getWorkspace().getTitle() + "<br>"
            + "Board Title : " + board.getTitle();
    }
}
