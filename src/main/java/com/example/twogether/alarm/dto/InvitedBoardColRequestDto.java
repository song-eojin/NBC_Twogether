package com.example.twogether.alarm.dto;

import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.alarm.entity.AlarmTrigger;
import com.example.twogether.user.entity.User;

public class InvitedBoardColRequestDto {

    public static Alarm toEntity(User invitingUser, User invitedUser, String title, String content, String url, AlarmTrigger alarmTrigger) {

        return Alarm.builder()
            .title(title)
            .content(content)
            .url(url)
            .alarmTrigger(alarmTrigger)
            .eventMaker(invitingUser) // 보드의 오너
            .user(invitedUser)
            .build();
    }
}
