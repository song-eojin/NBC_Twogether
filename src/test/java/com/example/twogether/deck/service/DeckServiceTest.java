package com.example.twogether.deck.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.example.twogether.board.entity.Board;
import com.example.twogether.board.repository.BoardRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.deck.dto.DeckResponseDto;
import com.example.twogether.deck.dto.MoveDeckRequestDto;
import com.example.twogether.deck.entity.Deck;
import com.example.twogether.deck.repository.DeckRepository;
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
public class DeckServiceTest {
    @Autowired
    private DeckService deckService;
    @Autowired
    private DeckRepository deckRepository;
    @Autowired
    private BoardRepository boardRepository;
    private static final float CYCLE = 128f;
    Board board;
    @BeforeEach
    void setUp() {
        board = boardRepository.findById(1L).orElse(null);
    }

    @Test
    @DisplayName("덱 생성 테스트")
    void addTest() {

        String title = "test1";

        deckService.addDeck(board.getId(), title);
        List<Deck> decks = deckRepository.findAll();

        assertEquals(title, decks.get(decks.size()-1).getTitle());
    }

    @Test
    @DisplayName("덱 단일 조회 테스트")
    void getTest() {
        List<Deck> decks = deckRepository.findAll();

        DeckResponseDto responseDto1 = deckService.getDeck(decks.get(0).getId());
        DeckResponseDto responseDto2 = deckService.getDeck(decks.get(1).getId());
        DeckResponseDto responseDto3 = deckService.getDeck(decks.get(2).getId());

        assertEquals("Deck 1", responseDto1.getTitle());
        assertEquals("Deck 2", responseDto2.getTitle());
        assertEquals("Deck 3", responseDto3.getTitle());
    }

    @Test
    @DisplayName("덱 수정 테스트")
    void editTest() {
        List<Deck> decks = deckRepository.findAll();
        Deck target = decks.get(0);
        String title = target.getTitle();

        deckService.editDeck(target.getId(), "edited" + title);

        assertEquals("edited" + title, target.getTitle());
    }

    @Test
    @DisplayName("덱 이동 테스트 (1 > 0)")
    void moveTest1() {
        List<Deck> decks = deckRepository.findAll();
        Deck target = decks.get(1);
        Deck next = decks.get(0);
        MoveDeckRequestDto requestDto = new MoveDeckRequestDto();
        requestDto.setPrevDeckId(0L);
        requestDto.setNextDeckId(next.getId());

        deckService.moveDeck(target.getId(), requestDto);

        assertEquals(next.getPosition() / 2f, target.getPosition());
    }

    @Test
    @DisplayName("덱 이동 테스트 (0 > 2)")
    void moveTest2() {
        List<Deck> decks = deckRepository.findAll();
        Deck target = decks.get(1);
        Deck prev = decks.get(2);
        MoveDeckRequestDto requestDto = new MoveDeckRequestDto();
        requestDto.setPrevDeckId(prev.getId());
        requestDto.setNextDeckId(0L);

        deckService.moveDeck(target.getId(), requestDto);

        assertEquals(prev.getPosition() + CYCLE, target.getPosition());
    }

    @Test
    @DisplayName("덱 이동 테스트 (2 > 1)")
    void moveTest3() {
        List<Deck> decks = deckRepository.findAll();
        Deck target = decks.get(1);
        Deck prev = decks.get(0);
        Deck next = decks.get(2);
        MoveDeckRequestDto requestDto = new MoveDeckRequestDto();
        requestDto.setNextDeckId(next.getId());
        requestDto.setPrevDeckId(prev.getId());

        deckService.moveDeck(target.getId(), requestDto);

        assertEquals((prev.getPosition() + next.getPosition()) / 2f, target.getPosition());
    }

    @Test
    @DisplayName("덱 보관/복구 테스트")
    void archiveTest() {
        List<Deck> decks = deckRepository.findAll();
        Deck target = decks.get(0);
        boolean archived = target.isArchived();

        deckService.archiveDeck(target.getId());

        assertEquals(!archived, target.isArchived());
    }

    @Test
    @DisplayName("덱 삭제 테스트")
    void deleteTest() {
        Long targetId = 4L;

        deckService.deleteDeck(targetId);

        assertNull(deckRepository.findById(targetId).orElse(null));
    }

    @Test
    @DisplayName("보관 안 된 덱 삭제 테스트")
    void deleteFailTest() {
        Long targetId = 1L;

        try {
            deckService.deleteDeck(targetId);
        } catch (CustomException e) {
            assertEquals(CustomErrorCode.DECK_IS_NOT_ARCHIVE, e.getErrorCode());
        }
    }
}
