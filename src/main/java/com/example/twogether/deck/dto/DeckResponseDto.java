package com.example.twogether.deck.dto;

import com.example.twogether.Card.dto.CardResponseDto;
import com.example.twogether.deck.entity.Deck;
import java.util.List;
import lombok.Getter;

@Getter
public class DeckResponseDto {
    private String title;
    private List<CardResponseDto> cardList;

    public DeckResponseDto(Deck deck) {
        this.title = deck.getTitle();
        this.cardList = deck.getCardList().stream().map(CardResponseDto::of).toList();
    }
}
