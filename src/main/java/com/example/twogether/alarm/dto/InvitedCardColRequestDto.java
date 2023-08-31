package com.example.twogether.alarm.dto;

import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.alarm.entity.AlarmTrigger;
import com.example.twogether.user.entity.User;

public class InvitedCardColRequestDto {

    public static Alarm toEntity(User boardUser, User addedUser, String content, String url, AlarmTrigger alarmTrigger, Long cardId, String cardTitle) {

        return Alarm.builder()
            .content(content)
            .url(url)
            .alarmTrigger(alarmTrigger)
            .eventMaker(boardUser) // 보드의 오너
            .user(addedUser)
            .cardId(cardId)
            .cardTitle(cardTitle)
            .build();
    }
}
