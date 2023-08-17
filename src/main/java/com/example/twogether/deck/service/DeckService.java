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
            isGone(id);
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

    @Transactional
    public void moveDeck(Long id, Long parentId) {
        isGone(id);

        Deck pushed = deckRepository.findByParentId(parentId).orElse(null);
        if (pushed != null) { // 덱을 맨 끝으로 이동한다면 밀려날 덱이 없으므로 그 경우를 고려한다.
            pushed.setParentId(id);
        }
        // 이동하고자 하는 덱에 의해 밀려나 덱의 parentId를 이동하고자 하는 덱의 id로 바꾸는 작업을 먼저 진행한다.
        // 이 작업을 나중에 하면 이동하고자 하는 덱과 밀려난 덱이 같은 parentId를 가지기 때문에 findByParentId가 에러를 발생시키기 때문

        Deck deck = findDeckById(id);
        deck.setParentId(parentId); // 이동하고자 하는 덱의 parentId를 수정
    }

    // 이동/삭제하고자 하는 덱의 id를 parentId로 가지고 있던 덱이 있다면, 그 값을 이동하고자 하는 덱의 parentId로 교환해줘야 한다.
    private void isGone(Long id) {
        // 이동하고자 하는 덱의 id를 parentId로 가지고 있던 덱이 없는 경우를 고려한다.
        Deck succeeded = deckRepository.findByParentId(id).orElse(null);
        if (succeeded != null) {
            Long parentIdForSucceed = findDeckById(id).getParentId();
            succeeded.setParentId(parentIdForSucceed);
        }
    }

    private Deck findDeckById(Long id) {
        Deck deck = deckRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException()
        );
        return deck;
    }
}
