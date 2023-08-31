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
    private final String content;

    @Builder
    public InvitedCardColEvent(Object source, User loginUser, User addedUser, Card card) {
        super(source);
        this.loginUser = loginUser;
        this.addedUser = addedUser;
        this.card = card;
        this.content = generateContent(loginUser, addedUser, card);
    }

    private String generateContent(User loginUser, User addedUser, Card card) {

        return "보드 오너(" + loginUser.getNickname() + ")가 "
            + "당신(" + addedUser.getNickname() + ")을 "
            + "\'ID" + card.getId() + ". " + card.getTitle() + "\' 카드에 작업자로 할당했습니다.";
    }
}
