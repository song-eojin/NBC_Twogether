package com.example.twogether.alarm.dto;

import com.example.twogether.alarm.entity.Alarm;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AlarmResponseDto {

    private Long id;
    private String title;
    private String content;
    private String url;
    private Boolean isRead;

    public static AlarmResponseDto of(Alarm alarm) {
        return AlarmResponseDto.builder()
            .id(alarm.getId())
            .title(alarm.getTitle())
            .content(alarm.getContent())
            .url(alarm.getUrl())
            .isRead(alarm.getIsRead())
            .build();
    }
}
