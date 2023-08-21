package com.example.twogether.card.dto;

import lombok.Getter;

@Getter
public class MoveCardRequestDto {
    private Long prevCardId;
    private Long nextCardId;
    private Long deckId;
}
