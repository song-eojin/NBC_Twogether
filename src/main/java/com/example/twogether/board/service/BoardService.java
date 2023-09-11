package com.example.twogether.board.service;

import com.example.twogether.board.dto.BoardRequestDto;
import com.example.twogether.board.dto.BoardResponseDto;
import com.example.twogether.board.entity.Board;
import com.example.twogether.board.entity.BoardCollaborator;
import com.example.twogether.board.repository.BoardColRepository;
import com.example.twogether.board.repository.BoardRepository;
import com.example.twogether.card.repository.CardLabelRepository;
import com.example.twogether.card.repository.CardRepository;
import com.example.twogether.checklist.repository.CheckListRepository;
import com.example.twogether.checklist.repository.ChlItemRepository;
import com.example.twogether.comment.repository.CommentRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.deck.repository.DeckRepository;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.entity.UserRoleEnum;
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.entity.WorkspaceCollaborator;
import com.example.twogether.workspace.repository.WpRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final DeckRepository deckRepository;
    private final CardLabelRepository cardLabelRepository;
    private final CommentRepository commentRepository;
    private final CheckListRepository checkListRepository;
    private final ChlItemRepository chlItemRepository;
    private final CardRepository cardRepository;
    private final BoardColRepository boardColRepository;
    private final WpRepository wpRepository;


    // 보드 생성
    @Transactional
    public void createBoard(User user, Long wpId, BoardRequestDto boardRequestDto) {

        Workspace foundWorkspace = findWorkspace(wpId);
        checkWpPermissions(foundWorkspace, user);

        Board foundBoard = boardRequestDto.toEntity(user, foundWorkspace);
        boardRepository.save(foundBoard);
        log.info("칸반 보드 생성에 성공했습니다.");

        // 보드 협업자 자동 등록
        List<WorkspaceCollaborator> workspaceCollaborators = foundWorkspace.getWorkspaceCollaborators();
        for (WorkspaceCollaborator workspaceCollaborator : workspaceCollaborators) {
            if (!boardColRepository.existsByBoardAndEmail(foundBoard, workspaceCollaborator.getUser().getEmail())) {
                BoardCollaborator newBoardCollaborator = BoardCollaborator.builder()
                    .email(workspaceCollaborator.getUser().getEmail())
                    .user(workspaceCollaborator.getUser())
                    .board(foundBoard)
                    .build();
                boardColRepository.save(newBoardCollaborator);
            }
        }
    }

    // 보드 수정
    @Transactional
    public Board editBoard(User user, Long boardId, BoardRequestDto boardRequestDto) {

        Board foundBoard = findBoard(boardId);
        checkBoardPermissions(foundBoard, user);

        if (boardRequestDto.getTitle() != null) {
            foundBoard.editTitle(boardRequestDto);
        }
        if (boardRequestDto.getColor() != null) {
            foundBoard.editColor(boardRequestDto);
        }
        if (boardRequestDto.getInfo() != null) {
            foundBoard.editInfo(boardRequestDto);
        }
        return foundBoard;
    }

    // 보드 삭제
    @Transactional
    public void deleteBoard(User user, Long boardId) {

        Board foundBoard = findBoard(boardId);
        if (!foundBoard.getUser().getEmail().equals(user.getEmail())) {
            throw new CustomException(CustomErrorCode.NOT_YOUR_BOARD);
        }

        deckRepository.findAllByBoard_Id(boardId).forEach(
            deck -> {
                cardRepository.findAllByDeck_Id(deck.getId()).forEach(
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
            }
        );
        boardColRepository.deleteAllByBoard_Id(boardId);
        boardRepository.delete(foundBoard);
    }

    // 보드 단일 조회
    @Transactional(readOnly = true)
    public BoardResponseDto getBoard(User user, Long boardId) {

        Board foundBoard = findBoard(boardId);
        checkBoardPermissions(foundBoard, user);

        return BoardResponseDto.of(foundBoard);
    }

    private Workspace findWorkspace(Long wpId) {

        return wpRepository.findById(wpId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.WORKSPACE_NOT_FOUND));
    }

    private Board findBoard(Long boardId) {

        return boardRepository.findById(boardId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
    }

    private void checkWpPermissions(Workspace workspace, User user) {

        if (!workspace.getUser().getEmail().equals(user.getEmail()) &&
            !workspace.getWorkspaceCollaborators().contains(user) &&
            !user.getRole().equals(UserRoleEnum.ADMIN)) {

            throw new CustomException(CustomErrorCode.NOT_PARTICIPATED_WORKSPACE);
        }
    }

    private void checkBoardPermissions(Board board, User user) {

        if (!board.getUser().getEmail().equals(user.getEmail()) &&
            !board.getBoardCollaborators().contains(user) &&
            !user.getRole().equals(UserRoleEnum.ADMIN)) {

            throw new CustomException(CustomErrorCode.NOT_PARTICIPATED_BOARD);
        }
    }
}
