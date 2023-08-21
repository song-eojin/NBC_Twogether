package com.example.twogether.card.service;

import com.example.twogether.card.dto.CardEditRequestDto;
import com.example.twogether.card.dto.MoveCardRequestDto;
import com.example.twogether.card.entity.Card;
import com.example.twogether.card.repository.CardRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.card.dto.CardResponseDto;
import com.example.twogether.deck.entity.Deck;
import com.example.twogether.deck.repository.DeckRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private static final float CYCLE = 128f;

    public void addCard(Long deckId, String title) {
        float max = findMaxPosition(deckId);
        Deck deck = findDeckById(deckId);
        Card newCard;

        if(max < 0)
            newCard = Card.builder().title(title).position(CYCLE).deck(deck).build();
        else
            newCard = Card.builder().title(title).position(max + CYCLE).deck(deck).build();

        cardRepository.save(newCard);
    }

    private Card findCardById(Long id) {
        return cardRepository.findById(id).orElseThrow(() ->
            new CustomException(CustomErrorCode.CARD_NOT_FOUND)
        );
    }

    private Deck findDeckById(Long id) {
        return deckRepository.findById(id).orElseThrow(() ->
            new CustomException(CustomErrorCode.DECK_NOT_FOUND)
        );
    }

    private float findMaxPosition(Long deckId) {
        float max = -1;
        List<Card> cards = cardRepository.findAllByDeck_Id(deckId);
        if (!cards.isEmpty()) {
            for (Card card : cards)
                max = Math.max(max, card.getPosition());
        }
        return max;
    }

    @Transactional(readOnly = true)
    public CardResponseDto getCard(Long id) {
        Card card = findCardById(id);
        return CardResponseDto.of(card);
    }

    @Transactional
    public void editCard(Long id, CardEditRequestDto requestDto) {
        Card card = findCardById(id);
        if (requestDto.getTitle() != null) card.editTitle(requestDto.getTitle());
        if (requestDto.getDescription() != null) card.editDescription(requestDto.getDescription());
    }

    public void deleteCard(Long id) {
        Card card = findCardById(id);
        if (card.isArchived()) {
            cardRepository.delete(card);
        } else {
            throw new CustomException(CustomErrorCode.CARD_IS_NOT_ARCHIVE);
        }
    }

    @Transactional
    public void archiveCard(Long id) {
        Card card = findCardById(id);
        card.archive();
    }

    @Transactional
    public void moveCard(Long id, MoveCardRequestDto requestDto) {
        Card card = findCardById(id);
        Deck deck = findDeckById(requestDto.getDeckId());

        Card prev = cardRepository.findById(requestDto.getPrevCardId()).orElse(null);
        Card next = cardRepository.findById(requestDto.getNextCardId()).orElse(null);

        if (requestDto.getDeckId() != null) card.moveToDeck(deck);
        if (prev != null && next != null) { // 두 카드 사이로 옮길 때
            card.editPosition((prev.getPosition() + next.getPosition()) / 2f);
        } else if (prev == null) { // 맨 처음으로 옮길 때
            card.editPosition(next.getPosition() / 2f);
        } else { // 맨 마지막으로 옮길 때
            card.editPosition(prev.getPosition() + CYCLE);
        }
    }
}
