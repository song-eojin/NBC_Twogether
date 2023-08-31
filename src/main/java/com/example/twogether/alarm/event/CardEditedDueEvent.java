package com.example.twogether.alarm.event;

import com.example.twogether.board.entity.Board;
import com.example.twogether.card.entity.Card;
import com.example.twogether.card.entity.CardCollaborator;
import com.example.twogether.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CardEditedDueEvent extends ApplicationEvent {

    private final User editor;
    private final User alarmTarget;
    private final Card card;
    private final LocalDateTime oldDue;
    private final LocalDateTime newDue;
    private final String content;

    @Builder
    public CardEditedDueEvent(Object source, User editor, User targetUser, Card card, LocalDateTime oldDue, LocalDateTime newDue) {
        super(source);
        this.editor = editor;
        this.alarmTarget = targetUser;
        this.card = card;
        this.oldDue = oldDue;
        this.newDue = newDue;
        this.content = generateContent(editor, card, oldDue, newDue);
    }

    private String generateContent(User editor, Card card, LocalDateTime oldDue, LocalDateTime newDue) {

        return editor.getNickname() + "가 "
            + "\'ID" + card.getId() + ". " + card.getTitle() + "\' 카드의 마감일을 "
            + oldDue + "에서 " + newDue + "로 수정했습니다.";
    }
}
