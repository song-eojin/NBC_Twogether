package com.example.twogether.alarm.dto;

import com.example.twogether.alarm.entity.AlarmTarget;
import com.example.twogether.board.entity.BoardCollaborator;
import com.example.twogether.user.entity.User;

public class AlarmTargetRequestDto {

    public static AlarmTarget userToEntity(User user) {

        return AlarmTarget.builder()
            .user(user)
            .build();
    }

    public static AlarmTarget boardColToEntity(BoardCollaborator boardCollaborator) {

        return AlarmTarget.builder()
            .user(boardCollaborator.getUser())
            .build();
    }
}
