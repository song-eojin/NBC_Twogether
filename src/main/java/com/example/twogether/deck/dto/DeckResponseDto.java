package com.example.twogether.deck.dto;

import com.example.twogether.deck.entity.Deck;
import lombok.Getter;

@Getter
public class DeckResponseDto {
    private String title;
//    private List<Card> cardList;

    public DeckResponseDto(Deck deck) {
        this.title = deck.getTitle();
//        this.cardList = deck.getCardlist().stream.map(CardResponseDto::of).toList();
    }
}
