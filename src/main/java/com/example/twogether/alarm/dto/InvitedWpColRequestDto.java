package com.example.twogether.alarm.dto;

import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.alarm.entity.AlarmTrigger;
import com.example.twogether.user.entity.User;

public class InvitedWpColRequestDto {

    public static Alarm toEntity(User invitingUser, User invitedUser, String content, String url, AlarmTrigger alarmTrigger, Long wpId, String wpTitle) {

        return Alarm.builder()
            .content(content)
            .url(url)
            .alarmTrigger(alarmTrigger)
            .eventMaker(invitingUser) // 워크스페이스의 오너
            .user(invitedUser)
            .wpId(wpId)
            .wpTitle(wpTitle)
            .build();
    }

}
