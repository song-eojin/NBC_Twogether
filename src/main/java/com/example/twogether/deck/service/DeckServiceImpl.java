package com.example.twogether.deck.service;

import com.example.twogether.deck.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeckServiceImpl {

    private final DeckRepository deckRepository;
}
