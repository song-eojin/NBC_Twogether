package com.example.twogether.deck.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveDeckRequestDto {
    private Long prevDeckId;
    private Long nextDeckId;
}