package com.example.twogether.deck.dto;


import lombok.Getter;

@Getter
public class MoveDeckRequestDto {
    private Long prevDeckId;
    private Long nextDeckId;
}