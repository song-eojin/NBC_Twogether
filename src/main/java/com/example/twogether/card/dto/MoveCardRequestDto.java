package com.example.twogether.card.dto;

import com.example.twogether.card.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MoveCardRequestDto {
    private Long prevCardId;
    private Long nextCardId;
    private Long deckId;
}
