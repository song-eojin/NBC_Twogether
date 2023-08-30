package com.example.twogether.board.service;

import com.example.twogether.board.dto.BoardRequestDto;
import com.example.twogether.board.dto.BoardResponseDto;
import com.example.twogether.board.entity.Board;
import com.example.twogether.board.repository.BoardRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.repository.UserRepository;
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.repository.WpRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
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
public class BoardServiceTest {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WpRepository wpRepository;

    private User user;
    private Workspace wp1;
    private Workspace wp2;
    private Workspace wp3;

    @BeforeEach
    void setUp() {
        user = userRepository.findById(1L).orElse(null);
        wp1 = wpRepository.findById(1L).orElse((null));
        wp2 = wpRepository.findById(2L).orElse((null));
        wp3 = wpRepository.findById(3L).orElse((null));
    }

    @Test
    @DisplayName("보드 생성 테스트")
    void createTest() {

        String title = "title 1";
        String color = "color 1";
        String info = "This is " + user.getNickname() + "'s " + title + " board.";

        BoardRequestDto boardRequestDto = BoardRequestDto.builder()
            .title(title)
            .color(color)
            .info(info)
            .build();

        boardService.createBoard(user, wp1.getId(), boardRequestDto);
        List<Board> boards = boardRepository.findAll();

        Assertions.assertNotNull(boards.get(boards.size()-1).getId());
        Assertions.assertEquals(title, boards.get(boards.size()-1).getTitle());
        Assertions.assertEquals(color, boards.get(boards.size()-1).getColor());
        Assertions.assertEquals(info, boards.get(boards.size()-1).getInfo());
        Assertions.assertEquals(user.getNickname(), boards.get(boards.size()-1).getUser().getNickname());
        Assertions.assertEquals(wp1.getId(), boards.get(boards.size()-1).getWorkspace().getId());
    }

    @Test
    @DisplayName("보드 생성 실패 테스트 - 다른 유저가 생성한 워크스페이스에서 보드 생성")
    void createFailTest() {

        String title = "title 1";
        String color = "color 1";
        String info = "This is " + user.getNickname() + "'s " + title + " board.";

        BoardRequestDto boardRequestDto = BoardRequestDto.builder()
            .title(title)
            .color(color)
            .info(info)
            .build();

        try {
            boardService.createBoard(user, wp3.getId(), boardRequestDto);
        } catch (CustomException e) {
            Assertions.assertEquals(CustomErrorCode.NOT_PARTICIPATED_WORKSPACE, e.getErrorCode());
        }
    }


    @Test
    @DisplayName("보드 수정 테스트")
    void editTest() {
        List<Board> boards = boardRepository.findAll();
        Board board = boards.get(0);
        String editTitle = "Edited Title";
        String editColor = "Edited Color";
        String editInfo = "This is " + user.getNickname() + "'s " + editTitle + " board.";

        BoardRequestDto boardRequestDto = BoardRequestDto.builder()
            .title(editTitle)
            .color(editColor)
            .info(editInfo)
            .build();

        boardService.editBoard(user, board.getId(), boardRequestDto);

        Assertions.assertEquals(editTitle, board.getTitle());
        Assertions.assertEquals(editColor, board.getColor());
        Assertions.assertEquals(editInfo, board.getInfo());
    }

    @Test
    @DisplayName("보드 수정 실패 테스트 1")
    void editFailTest1() {
        List<Board> boards = boardRepository.findAll();
        Board board = boards.get(2);
        String editTitle = "Edited Title";
        String editColor = "Edited Color";
        String editInfo = "This is " + user.getNickname() + "'s " + editTitle + " board.";

        BoardRequestDto boardRequestDto = BoardRequestDto.builder()
            .title(editTitle)
            .color(editColor)
            .info(editInfo)
            .build();

        try {
            boardService.editBoard(user, board.getId(), boardRequestDto);
        } catch (CustomException e) {
            Assertions.assertEquals(CustomErrorCode.BOARD_NOT_FOUND, e.getErrorCode());
        }
    }

    @Test
    @DisplayName("보드 수정 실패 테스트 2 - 존재하지 않는 boardId를 사용해서 테스트")
    void editFailTest2() {
        Long boardId = 4L;
        String editTitle = "Edited Title";
        String editColor = "Edited Color";
        String editInfo = "This is " + user.getNickname() + "'s " + editTitle + " board.";

        BoardRequestDto boardRequestDto = BoardRequestDto.builder()
            .title(editTitle)
            .color(editColor)
            .info(editInfo)
            .build();

        try {
            boardService.editBoard(user, boardId, boardRequestDto);
        } catch (CustomException e) {
            Assertions.assertEquals(CustomErrorCode.BOARD_NOT_FOUND, e.getErrorCode());
        }
    }

    @Test
    @DisplayName("보드 단건 조회 테스트")
    void getTest() {
        List<Board> boards = boardRepository.findAll();

        BoardResponseDto boardResponseDto1 = boardService.getBoard(user, boards.get(0).getId());
        BoardResponseDto boardResponseDto2 = boardService.getBoard(user, boards.get(1).getId());

        Assertions.assertEquals("Board 1", boardResponseDto1.getTitle());
        Assertions.assertEquals("black", boardResponseDto1.getColor());
        Assertions.assertEquals("Board 2", boardResponseDto2.getTitle());
        Assertions.assertEquals("white", boardResponseDto2.getColor());
    }

    @Test
    @DisplayName("보드 단건 조회 실패 테스트")
    void getFailTest() {
        Long boardId = 4L;

        try {
            boardService.getBoard(user, boardId);
        } catch (CustomException e) {
            Assertions.assertEquals(CustomErrorCode.BOARD_NOT_FOUND, e.getErrorCode());
        }
    }

    @Test
    @DisplayName("보드 삭제 테스트")
    void deleteTest() {
        Long boardId = 1L;
        List<Board> Boards = boardRepository.findAll();

        boardService.deleteBoard(user, boardId);

        List<Board> DeletedBoards = boardRepository.findAll();

        Assertions.assertNull(boardRepository.findById(boardId).orElse(null));

        // 1번 보드 삭제 시 남아있는 보드 전체가 DB에 정상적으로 있는지 확인
        for (int i = 0; i < Boards.size(); i++) {
            if (!Boards.get(i).getId().equals(boardId)) {
                Board Board = Boards.get(i);
                Board DeletedBoard = DeletedBoards.get(i-1);
                Assertions.assertEquals(Board.getTitle(), DeletedBoard.getTitle());
                Assertions.assertEquals(Board.getColor(), DeletedBoard.getColor());
                Assertions.assertEquals(Board.getInfo(), DeletedBoard.getInfo());
            }
        }
    }

    @Test
    @DisplayName("보드 삭제 실패 테스트 - 존재하지 않는 보드 삭제 테스트")
    void deleteFailTest() {
        Long boardId = 5L;

        List<Board> Boards = boardRepository.findAll();

        try {
            boardService.deleteBoard(user, boardId);
        } catch (CustomException e) {
            Assertions.assertEquals(CustomErrorCode.BOARD_NOT_FOUND, e.getErrorCode());
        }

        List<Board> DeletedBoards = boardRepository.findAll();

        // 보드 삭제 실패했을 때 보드 전체가 DB에 변경없이 정상적으로 있는지 확인
        for (int i = 0; i < Boards.size(); i++) {
            if (!Boards.get(i).getId().equals(boardId)) {
                Board Board = Boards.get(i);
                Board DeletedBoard = DeletedBoards.get(i);

                Assertions.assertEquals(Board.getTitle(), DeletedBoard.getTitle());
                Assertions.assertEquals(Board.getColor(), DeletedBoard.getColor());
                Assertions.assertEquals(Board.getInfo(), DeletedBoard.getInfo());
            }
        }
    }
}