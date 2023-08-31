package com.example.twogether.alarm.dto;

import com.example.twogether.alarm.entity.Alarm;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CardEditedResponseDto {

    private Long id;
    private String content;
    private String url;
    private Boolean isRead;

    public static CardEditedResponseDto of(Alarm alarm) {
        return CardEditedResponseDto.builder()
            .id(alarm.getId())
            .content(alarm.getContent())
            .url(alarm.getUrl())
            .isRead(alarm.getIsRead())
            .build();
    }
}
