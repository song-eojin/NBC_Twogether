package com.example.twogether.board.service;

import com.example.twogether.alarm.event.TriggerEventPublisher;
import com.example.twogether.board.dto.BoardColRequestDto;
import com.example.twogether.board.dto.BoardsResponseDto;
import com.example.twogether.board.entity.Board;
import com.example.twogether.board.entity.BoardCollaborator;
import com.example.twogether.board.repository.BoardColRepository;
import com.example.twogether.board.repository.BoardRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.entity.UserRoleEnum;
import com.example.twogether.user.repository.UserRepository;
import com.example.twogether.workspace.service.WpColService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardColService {

    private final BoardColRepository boardColRepository;
    private final BoardRepository boardRepository;
    private final WpColService wpColService;
    private final UserRepository userRepository;
    private final TriggerEventPublisher eventPublisher;
  
    // 칸반 보드에 협업자 초대 - 허락받아야 초대되는 로직으로 develop 할지 고민 중
    @Transactional
    public void inviteBoardCol(User user, Long boardId, String email) {

        Board board = findBoard(boardId);
        User invitee = findUser(email);
        confirmUser(board, user, email);

        BoardCollaborator collaborator = boardColRepository.findByBoardAndEmail(board, email)
            .orElse(null);

        // 이미 등록된 사용자 초대당하기 불가
        if (collaborator != null) {
            throw new CustomException(CustomErrorCode.BOARD_COLLABORATOR_ALREADY_EXISTS);
        }

        // 보드 협업자로 등록
        BoardCollaborator foundBoardCol = BoardColRequestDto.toEntity(invitee, board);
        boardColRepository.save(foundBoardCol);

        // 자동으로 상위 워크스페이스 협업자로도 등록
        wpColService.autoInviteWpCol(board.getWorkspace().getId(), email);
        eventPublisher.publishInviteBoardColEvent(user, invitee, board);
    }

    // 칸반 보드에서 협업자 추방
    @Transactional
    public void outBoardCol(User user, Long boardId, String email) {

        Board board = findBoard(boardId);
        confirmUser(board, user, email);

        // 보드 협업자 삭제
        BoardCollaborator collaborator = boardColRepository.findByBoardAndEmail(board, email)
            .orElse(null);
        // 등록되지 않은 사용자 추방하기 불가
        if (collaborator == null) {
            throw new CustomException(CustomErrorCode.BOARD_COLLABORATOR_NOT_FOUND);
        }

        // 보드 협업자 추방
        boardColRepository.delete(collaborator);

        // 자동으로 카드에 할당된 협업자 목록에서도 삭제
        board.getDecks().forEach(deck ->
            deck.getCards().forEach(card ->
                card.getCardCollaborators().removeIf(cardCol
                    -> cardCol.getEmail().equals(email))
            )
        );
    }

    // 초대된 보드 전체 조회
    @Transactional(readOnly = true)
    public BoardsResponseDto getBoardCols(User user) {

        List<Board> boards = boardColRepository.findAllByUser_Id(user.getId()).stream()
            .map(BoardCollaborator::getBoard).toList();

        return BoardsResponseDto.of(boards);
    }

    private Board findBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(() ->
            new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
            new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    private void confirmUser(Board board, User user, String email) {
        if (!board.getUser().getId().equals(user.getId()) &&
            !user.getRole().equals(UserRoleEnum.ADMIN)) {

            throw new CustomException(CustomErrorCode.NOT_YOUR_BOARD);
        }

        if (email.equals(user.getEmail())) {
            throw new CustomException(CustomErrorCode.THIS_IS_YOUR_BOARD);
        }
    }
}