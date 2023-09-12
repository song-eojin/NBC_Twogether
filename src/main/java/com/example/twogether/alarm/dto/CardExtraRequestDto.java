package com.example.twogether.alarm.dto;

import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.alarm.entity.AlarmTrigger;
import com.example.twogether.user.entity.User;

public class CardExtraRequestDto {

    public static Alarm toEntity(User editor, User alarmTarget, String title, String content, String url, AlarmTrigger alarmTrigger) {

        return Alarm.builder()
            .eventMaker(editor)
            .user(alarmTarget)
            .content(content)
            .title(title)
            .url(url)
            .alarmTrigger(alarmTrigger)
            .build();
    }
}
