package com.example.twogether.alarm.dto;

import com.example.twogether.alarm.entity.Alarm;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AlarmsResponseDto {

    private List<AlarmResponseDto> alarms;

    public static AlarmsResponseDto of(List<Alarm> alarms) {

        List<AlarmResponseDto> alarmsResponseDto = alarms.stream().map(
            AlarmResponseDto::of).toList();

        return AlarmsResponseDto.builder()
            .alarms(alarmsResponseDto)
            .build();
    }
}
