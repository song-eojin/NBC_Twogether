package com.example.twogether.label.dto;

import com.example.twogether.card.entity.CardLabel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CardLabelResponseDto {
    private Long cardId;
    private Long labelId;
    private String title;
    private String color;

    public static CardLabelResponseDto of(CardLabel cardLabel) {
        return CardLabelResponseDto.builder()
            .cardId(cardLabel.getCard().getId())
            .labelId(cardLabel.getLabel().getId())
            .title(cardLabel.getLabel().getTitle())
            .color(cardLabel.getLabel().getColor())
            .build();
    }
}
