package com.example.twogether.deck.service;

import com.example.twogether.board.entity.Board;
import com.example.twogether.board.repository.BoardRepository;
import com.example.twogether.card.repository.CardLabelRepository;
import com.example.twogether.card.repository.CardRepository;
import com.example.twogether.checklist.repository.CheckListRepository;
import com.example.twogether.checklist.repository.ChlItemRepository;
import com.example.twogether.comment.repository.CommentRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.deck.dto.DeckResponseDto;
import com.example.twogether.deck.dto.MoveDeckRequestDto;
import com.example.twogether.deck.entity.Deck;
import com.example.twogether.deck.repository.DeckRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeckService {

    private final BoardRepository boardRepository;
    private final DeckRepository deckRepository;
    private final CardLabelRepository cardLabelRepository;
    private final CommentRepository commentRepository;
    private final CheckListRepository checkListRepository;
    private final ChlItemRepository chlItemRepository;
    private final CardRepository cardRepository;
    private static final float CYCLE = 128f;

    @Transactional
    public void addDeck(Long boardId, String title) {
        float max = findMaxPosition(boardId);
        Board board = findBoardById(boardId);
        Deck newDeck;

        if(max < 0)
            newDeck = Deck.builder().title(title).position(CYCLE).board(board).build();
        else
            newDeck = Deck.builder().title(title).position(max + CYCLE).board(board).build();
      
        deckRepository.save(newDeck);
    }

    @Transactional(readOnly = true)
    public DeckResponseDto getDeck(Long id) {
        Deck deck = findDeckById(id);
        return DeckResponseDto.of(deck);
    }

    @Transactional
    public void editDeck(Long id, String title) {
        Deck deck = findDeckById(id);
        deck.editTitle(title);
    }

    @Transactional
    public void deleteDeck(Long id) {
        Deck deck = findDeckById(id);
        if (deck.isArchived()) {
            cardRepository.findAllByDeck_Id(id).forEach(
                card -> {
                    commentRepository.deleteAllByCard_Id(card.getId());
                    cardLabelRepository.deleteAllByCard_Id(card.getId());
                    checkListRepository.findAllByCardId(card.getId()).forEach(
                        checkList -> chlItemRepository.deleteAllByCheckList_Id(checkList.getId())
                    );
                    checkListRepository.deleteAllByCard_Id(card.getId());
                    cardRepository.delete(card);
                }
            );
            deckRepository.delete(deck);
        } else {
            throw new CustomException(CustomErrorCode.DECK_IS_NOT_ARCHIVE);
        }
    }

    @Transactional
    public void archiveDeck(Long id) {
        Deck deck = findDeckById(id);
        deck.archive();
    }

    @Transactional
    public void moveDeck(Long id, MoveDeckRequestDto requestDto) {
        Deck deck = findDeckById(id);

        Deck prev = deckRepository.findById(requestDto.getPrevDeckId()).orElse(null);
        Deck next = deckRepository.findById(requestDto.getNextDeckId()).orElse(null);

        if (prev != null && next != null) { // 두 덱 사이로 옮길 때
            deck.editPosition((prev.getPosition() + next.getPosition()) / 2f);
        } else if (prev == null) { // 맨 처음으로 옮길 때
            deck.editPosition(Objects.requireNonNull(next).getPosition() / 2f);
        } else { // 맨 마지막으로 옮길 때
            deck.editPosition(prev.getPosition() + CYCLE);
        }
    }

    private Deck findDeckById(Long id) {
        return deckRepository.findById(id).orElseThrow(() ->
            new CustomException(CustomErrorCode.DECK_NOT_FOUND)
        );
    }

    private Board findBoardById(Long id) {
        return boardRepository.findById(id).orElseThrow(() ->
            new CustomException(CustomErrorCode.BOARD_NOT_FOUND)
        );
    }

    private float findMaxPosition(Long boardId) {
        float max = -1;
        List<Deck> decks = deckRepository.findAllByBoard_Id(boardId);
        if (!decks.isEmpty()) {
            for (Deck deck : decks)
                max = Math.max(max, deck.getPosition());
        }
        return max;
    }
}
