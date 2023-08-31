package com.example.twogether.alarm.dto;

import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.alarm.entity.AlarmTrigger;
import com.example.twogether.user.entity.User;

public class CardEditedRequestDto {

    public static Alarm toEntity(User user, User alarmTarget, String content, String url, AlarmTrigger alarmTrigger, boolean isRead) {

        return Alarm.builder()
            .eventMaker(user)
            .user(alarmTarget)
            .content(content)
            .url(url)
            .alarmTrigger(alarmTrigger)
            .isRead(isRead)
            .build();
    }
}
