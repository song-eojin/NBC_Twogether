package com.example.twogether.alarm.event;

import com.example.twogether.alarm.entity.AlarmTarget;
import com.example.twogether.card.entity.Card;
import com.example.twogether.comment.entity.Comment;
import com.example.twogether.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.type.descriptor.java.ObjectJavaType;
import org.springframework.context.ApplicationEvent;

@Getter
public class CardCommentEvent extends ApplicationEvent {

    private final User editor;
    private final User alarmTarget;
    private final Card card;
    private final Comment comment;
    private final String title;
    private final String content;

    @Builder
    public CardCommentEvent(Object source, User editor, User targetUser, Card card, Comment comment) {

        super(source);
        this.editor = editor;
        this.alarmTarget = targetUser;
        this.card = card;
        this.comment = comment;
        this.title = "Added Comment";
        this.content = generateContent(card, comment);
    }

    private String generateContent(Card card, Comment comment) {

        return "Card Title : " + card.getTitle() + "<br>"
            + "Comment Content : " + comment.getContent();
    }
}
