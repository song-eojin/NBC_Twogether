package com.example.twogether.deck.dto;

import com.example.twogether.card.dto.CardResponseDto;
import com.example.twogether.deck.entity.Deck;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeckResponseDto {
    private Long deckId;
    private String title;
    private float position;
    private boolean archived;
    private List<CardResponseDto> cards;

    public static DeckResponseDto of(Deck deck) {
        return DeckResponseDto.builder()
            .deckId(deck.getId())
            .title(deck.getTitle())
            .position(deck.getPosition())
            .archived(deck.isArchived())
            .cards(deck.getCards().stream().map(CardResponseDto::of).
                sorted(Comparator.comparing(CardResponseDto::getPosition)).toList())
            .build();
    }
}
