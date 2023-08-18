package com.example.twogether.deck.entity;

import java.util.LinkedList;

public class DeckManager {
    private LinkedList<Deck> deckList;

    public DeckManager() {
        deckList = new LinkedList<>();
    }

    public void addDeck(Deck newDeck) {
        if (!deckList.isEmpty()) {
            newDeck.setParentId(deckList.getLast().getId());
        }

        deckList.add(newDeck);
    }

}
