package com.example.twogether.label.dto;

import com.example.twogether.label.entity.Label;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LabelResponseDto {
    private Long labelId;
    private String title;
    private String color;

    public static LabelResponseDto of(Label label) {
        return LabelResponseDto.builder()
                .labelId(label.getId())
                .title(label.getTitle())
                .color(label.getColor())
                .build();
    }
}
