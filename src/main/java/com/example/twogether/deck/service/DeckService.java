package com.example.twogether.deck.service;

import com.example.twogether.deck.dto.DeckRequestDto;
import com.example.twogether.deck.dto.DeckResponseDto;
import com.example.twogether.deck.entity.Deck;
import com.example.twogether.deck.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final DeckRepository deckRepository;

    public void addDeck(DeckRequestDto requestDto) {
        Deck deck =  requestDto.toEntity();
        deckRepository.save(deck);
    }

    public DeckResponseDto getDeck(Long id) {
        Deck deck = findDeckById(id);
        return new DeckResponseDto(deck);
    }

    private Deck findDeckById(Long id) {
        Deck deck = deckRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException()
        );
        return deck;
    }

    public void editDeck(Long id, String title) {
        Deck deck = findDeckById(id);
        deck.editTitle(title);
    }
}
