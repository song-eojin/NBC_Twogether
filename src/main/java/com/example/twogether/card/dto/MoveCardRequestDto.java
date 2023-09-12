package com.example.twogether.card.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MoveCardRequestDto {
    private Long prevCardId;
    private Long nextCardId;
    private Long deckId;
}
