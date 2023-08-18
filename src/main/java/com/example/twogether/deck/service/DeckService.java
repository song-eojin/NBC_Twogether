package com.example.twogether.deck.service;

import com.example.twogether.deck.dto.DeckResponseDto;
import com.example.twogether.deck.entity.Deck;
import com.example.twogether.deck.entity.DeckManager;
import com.example.twogether.deck.repository.DeckRepository;
import java.util.LinkedList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeckService {

    private DeckManager deckManager = new DeckManager();
    private final DeckRepository deckRepository;

    public void addDeck(String title) {
        Deck newDeck = new Deck(title);
        deckManager.addDeck(newDeck);

        deckRepository.save(newDeck);
    }

    @Transactional(readOnly = true)
    public DeckResponseDto getDeck(Long id) {
        Deck deck = findDeckById(id);
        return new DeckResponseDto(deck);
    }

    @Transactional
    public void editDeck(Long id, String title) {
        Deck deck = findDeckById(id);
        deck.editTitle(title);
    }

    public void deleteDeck(Long id) {
        Deck deck = findDeckById(id);
        if (deck.isDeleted()) {
            deckRepository.delete(deck);
        } else {
            throw new RuntimeException("덱이 deleted 상태일 때만 삭제 가능합니다.");
        }
    }

    @Transactional
    public void archiveDeck(Long id) {
        Deck deck = findDeckById(id);
        deck.archive();
    }

    private Deck findDeckById(Long id) {
        Deck deck = deckRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException()
        );
        return deck;
    }
}
