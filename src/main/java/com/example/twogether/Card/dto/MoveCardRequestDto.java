package com.example.twogether.Card.dto;

import lombok.Getter;

@Getter
public class MoveCardRequestDto {
    private Long prevCardId;
    private Long nextCardId;
    private Long deckId;
}
