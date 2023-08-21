package com.example.twogether.Card.dto;

import com.example.twogether.Card.entity.Card;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String attachment;
    private float position;

    public static CardResponseDto of(Card card) {
        return CardResponseDto.builder()
            .id(card.getId())
            .title(card.getTitle())
            .description(card.getDescription())
            .dueDate(card.getDueDate())
            .attachment(card.getAttachment())
            .position(card.getPosition())
            .build();
    }

}
