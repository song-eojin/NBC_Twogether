package com.example.twogether.alarm.event;

import com.example.twogether.board.entity.Board;
import com.example.twogether.card.entity.Card;
import com.example.twogether.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class InvitedCardColEvent extends ApplicationEvent {

    private final User loginUser;
    private final User addedUser;
    private final Card card;
    private final String title;
    private final String content;

    @Builder
    public InvitedCardColEvent(Object source, User editor, User invitedUser, Card card) {
        super(source);
        this.loginUser = editor;
        this.addedUser = invitedUser;
        this.card = card;
        this.title = "Assigned as a Card Worker";
        this.content = generateContent(card);
    }

    private String generateContent(Card card) {

        return "Workspace Title : " + card.getDeck().getBoard().getWorkspace().getTitle() + "<br>"
            + "Board Title : " + card.getDeck().getBoard().getTitle() + "<br>"
            + "Card Title : " + card.getTitle();
    }
}
