package com.example.twogether.alarm.dto;

import com.example.twogether.alarm.entity.Alarm;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CardEditedsResponseDto {

    private List<CardEditedResponseDto> alarms;

    public static CardEditedsResponseDto of(List<Alarm> alarms) {

        List<CardEditedResponseDto> alarmsResponseDto = alarms.stream().map(
            CardEditedResponseDto::of).toList();

        return CardEditedsResponseDto.builder()
            .alarms(alarmsResponseDto)
            .build();
    }
}
