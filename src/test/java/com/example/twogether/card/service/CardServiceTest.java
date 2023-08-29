package com.example.twogether.card.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.twogether.board.entity.Board;
import com.example.twogether.board.repository.BoardRepository;
import com.example.twogether.card.dto.CardEditRequestDto;
import com.example.twogether.card.dto.CardResponseDto;
import com.example.twogether.card.dto.DateRequestDto;
import com.example.twogether.card.dto.MoveCardRequestDto;
import com.example.twogether.card.entity.Card;
import com.example.twogether.card.repository.CardRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.deck.entity.Deck;
import com.example.twogether.deck.repository.DeckRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CardServiceTest {
    @Autowired
    private CardService cardService;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private BoardRepository boardRepository;

    private static final float CYCLE = 128f;
    Board board;
    Deck deck1;
    Deck deck2;
    Deck deck3;

    @BeforeEach
    void setUp() {
        board = boardRepository.findById(1L).orElse(null);
        deck1 = deckRepository.findById(1L).orElse(null);
        deck2 = deckRepository.findById(2L).orElse(null);
        deck3 = deckRepository.findById(3L).orElse(null);
    }

    @Test
    @DisplayName("카드 생성 테스트")
    void addTest() {
        String title = "test 1";

        cardService.addCard(deck1.getId(), title);
        List<Card> cards = cardRepository.findAll();

        assertEquals(title, cards.get(cards.size()-1).getTitle());
    }

    @Test
    @DisplayName("카드 조회 테스트")
    void getTest() {
        List<Card> cards = cardRepository.findAll();

        CardResponseDto responseDto1 = cardService.getCard(cards.get(0).getId());
        CardResponseDto responseDto2 = cardService.getCard(cards.get(1).getId());
        CardResponseDto responseDto3 = cardService.getCard(cards.get(2).getId());

        assertEquals("Card 1-1", responseDto1.getTitle());
        assertEquals("Card 1-2", responseDto2.getTitle());
        assertEquals("Card 1-3", responseDto3.getTitle());
    }

    @Test
    @DisplayName("카드 수정 테스트")
    void editTest() {
        List<Card> cards = cardRepository.findAll();
        Card target = cards.get(0);
        String editTitle = "edited title";
        String editDsc = "edited description";
        CardEditRequestDto requestDto = new CardEditRequestDto();
        requestDto.setTitle(editTitle);
        requestDto.setDescription(editDsc);

        cardService.editCard(target.getId(), requestDto);

        assertEquals(editTitle, target.getTitle());
        assertEquals(editDsc, target.getDescription());
    }

    @Test
    @DisplayName("카드 보관/복구 테스트")
    void archiveTest() {
        List<Card> cards = cardRepository.findAll();
        Card target = cards.get(0);
        boolean archived = target.isArchived();

        cardService.archiveCard(target.getId());

        assertEquals(!archived, target.isArchived());
    }

    @Test
    @DisplayName("카드 삭제 테스트")
    void deleteTest() {
        Long targetId = 6L;

        cardService.deleteCard(targetId);

        assertNull(deckRepository.findById(targetId).orElse(null));
    }

    @Test
    @DisplayName("카드 삭제 실패 테스트")
    void deleteFailTest() {
        Long targetId = 1L;

        try {
            cardService.deleteCard(targetId);
        } catch (CustomException e) {
            assertEquals(CustomErrorCode.CARD_IS_NOT_ARCHIVE, e.getErrorCode());
        }
    }

    @Test
    @DisplayName("카드 이동 테스트 (Board 1 2 > Board 1 0)")
    void moveTest1() {
        List<Card> cards = cardRepository.findAll();
        Card target = cards.get(2);
        Card next = cards.get(0);
        MoveCardRequestDto requestDto = MoveCardRequestDto.builder()
            .prevCardId(0L)
            .nextCardId(next.getId())
            .deckId(deck1.getId())
            .build();

        cardService.moveCard(target.getId(), requestDto);

        assertEquals(next.getPosition() / 2f, target.getPosition());
        assertEquals(deck1.getId(), target.getDeck().getId());
    }

    @Test
    @DisplayName("카드 이동 테스트 (Board 1 0 > Board 1 2)")
    void moveTest2() {
        List<Card> cards = cardRepository.findAll();
        Card target = cards.get(0);
        Card prev = cards.get(2);
        MoveCardRequestDto requestDto = MoveCardRequestDto.builder()
            .prevCardId(prev.getId())
            .nextCardId(0L)
            .deckId(deck1.getId())
            .build();

        cardService.moveCard(target.getId(), requestDto);

        assertEquals(prev.getPosition() + CYCLE, target.getPosition());
        assertEquals(deck1.getId(), target.getDeck().getId());
    }

    @Test
    @DisplayName("카드 이동 테스트 (Board 1 2 > Board 1 1)")
    void moveTest3() {
        List<Card> cards = cardRepository.findAll();
        Card target = cards.get(2);
        Card prev = cards.get(0);
        Card next = cards.get(1);
        MoveCardRequestDto requestDto = MoveCardRequestDto.builder()
            .prevCardId(prev.getId())
            .nextCardId(next.getId())
            .deckId(deck1.getId())
            .build();

        cardService.moveCard(target.getId(), requestDto);

        assertEquals((prev.getPosition() + next.getPosition()) / 2f, target.getPosition());
        assertEquals(deck1.getId(), target.getDeck().getId());
    }

    @Test
    @DisplayName("카드 이동 테스트 (Board 1 1 > Board 2 1)")
    void moveTest4() {
        List<Card> cards = cardRepository.findAll();
        Card target = cards.get(1);
        Card prev = cards.get(4);
        Card next = cards.get(5);
        MoveCardRequestDto requestDto = MoveCardRequestDto.builder()
            .prevCardId(prev.getId())
            .nextCardId(next.getId())
            .deckId(deck2.getId())
            .build();

        cardService.moveCard(target.getId(), requestDto);

        assertEquals((prev.getPosition() + next.getPosition()) / 2f, target.getPosition());
        assertEquals(deck2.getId(), target.getDeck().getId());
    }

    @Test
    @DisplayName("마감일 설정 테스트")
    void dueDateTest() {
        List<Card> cards = cardRepository.findAll();
        Card target = cards.get(0);
        DateRequestDto requestDto = new DateRequestDto();
        requestDto.setDueDate(LocalDateTime.now());

        cardService.editDate(target.getId(), requestDto);

        assertEquals(requestDto.getDueDate(), target.getDueDate());
    }
}
