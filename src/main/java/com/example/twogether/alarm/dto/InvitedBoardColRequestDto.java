package com.example.twogether.alarm.dto;

import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.alarm.entity.AlarmTrigger;
import com.example.twogether.user.entity.User;

public class InvitedBoardColRequestDto {

    public static Alarm toEntity(User invitingUser, User invitedUser, String content, String url, AlarmTrigger alarmTrigger, Long boardId, String boardTitle) {

        return Alarm.builder()
            .content(content)
            .url(url)
            .alarmTrigger(alarmTrigger)
            .eventMaker(invitingUser) // 보드의 오너
            .user(invitedUser)
            .boardId(boardId)
            .boardTitle(boardTitle)
            .build();
    }
}
