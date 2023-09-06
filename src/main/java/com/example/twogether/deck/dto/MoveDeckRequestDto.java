package com.example.twogether.deck.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveDeckRequestDto {
    private Long prevDeckId;
    private Long nextDeckId;
}