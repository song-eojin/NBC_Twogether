package com.example.twogether.alarm.event;

import com.example.twogether.card.entity.Card;
import com.example.twogether.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CardEditedEvent extends ApplicationEvent {

    private final User user;
    private final User alarmTarget;
    private final Card card;
    private final String oldContent;
    private final String newContent;
    private final String title;
    private final String content;

    @Builder
    public CardEditedEvent(Object source, User editor, User targetUser, Card card, String oldContent, String newContent) {

        super(source);
        this.user = editor;
        this.alarmTarget = targetUser;
        this.card = card;
         this.oldContent = oldContent; // 카드 내용 수정 전
        this.newContent = newContent; // 카드 내용 수정후
        this.title = "Updated Comment's Content";
        this.content = generateContent(card, newContent); // 알림 내용
    }

    private String generateContent(Card card, String newContent) {

        return "Card Title : " + card.getTitle() + "<br>"
            + "Card Content : <br>"
            + "<span id='oldContent'>" + oldContent + "</span><br>"
            + "→<br>"
            + newContent;
    }

    /* (추후 작업) 추가기능 구현 준비 코드 - 자동 취소선, 형광펜 */
//    private String generateContent(String oldContent, String newContent) {
//
//        StringBuilder contentBuilder = new StringBuilder();
//
//        int oldIndex = 0;
//        int newIndex = 0;
//
//        while (newIndex < newContent.length()) {
//            if (oldContent != null) {
//                if (oldIndex < oldContent.length()
//                    && oldContent.charAt(oldIndex) == newContent.charAt(newIndex)) {
//                    contentBuilder.append(newContent.charAt(newIndex));
//                    oldIndex++;
//                    newIndex++;
//                } else if (oldContent.charAt(oldIndex) < newContent.charAt(newIndex)) {
//                    contentBuilder.append("<span style=\"background-color: lightyellow;\">")
//                        .append(oldContent.charAt(oldIndex)).append("</span>");
//                    oldIndex++;
//                }
//                while (newIndex < newContent.length()) {
//                    contentBuilder.append(newContent.charAt(newIndex));
//                    newIndex++;
//                }
//            } else {
//                contentBuilder.append("<span style=\"background-color: lightyellow;\">")
//                    .append(newContent.charAt(newIndex)).append("</span>");
//                newIndex++;
//            }
//        }
//        while (oldIndex < oldContent.length()) {
//            contentBuilder.append("<del>").append(oldContent.charAt(oldIndex)).append("</del>");
//            oldIndex++;
//        }
//
//        String remove = "</del><del>";
//        String replace = "";
//        int index = contentBuilder.indexOf(remove);
//
//        while (index != -1) {
//            contentBuilder.replace(index, index + remove.length(), replace);
//            index = contentBuilder.indexOf(remove, index + replace.length());
//        }
//
//        return contentBuilder.toString();
//    }
}
