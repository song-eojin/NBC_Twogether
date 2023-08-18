package com.example.twogether.board.service;

import com.example.twogether.board.dto.BoardResponseDto;
import com.example.twogether.board.dto.BoardsResponseDto;
import com.example.twogether.board.entity.Board;
import com.example.twogether.board.entity.BoardMember;
import com.example.twogether.common.security.UserDetailsImpl;
import com.example.twogether.user.entity.User;
import com.sun.jdi.request.DuplicateRequestException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

public class BoardMemberService {
    /* 보드 작업자 관련 */

    // 협업 초대받은 보드 전체 조회
/*    @Transactional(readOnly = true)
    public BoardsResponseDto getCollaboratedBoards(UserDetailsImpl userDetails) {
        try {
            List<BoardMember> boardMembers = boardMemberRepository.findByBoardCollabo(
                userDetails.getUser());

            List<Board> collaboratedBoards = boardMembers.stream()
                .map(BoardMember::getBoard)
                .collect(Collectors.toList());

            return BoardsResponseDto.of(collaboratedBoards);
        } catch (Exception e) {
            throw new RuntimeException("협업 중인 칸반 보드 조회를 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 협업 초대받은 보드 단건 조회
    @Transactional(readOnly = true)
    public BoardResponseDto getCollaboratedBoardById(User boardCollabo, Long id) {
        try {
            Board board = findBoard(boardCollabo, id);
            return BoardResponseDto.of(board);
        } catch (Exception e) {
            throw new RuntimeException("협업 중인 칸반 보드 조회를 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 협업 초대받은 보드 협업자 등록
    // 허락받아야 초대 가능한 로직으로 변경하기 - 추후 작업
    @Transactional
    public void addCollaborator(Long boardId, String email) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보드입니다."));
        System.out.println("check "+email);
        User collaborator = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        try {
            if (boardMemberRepository.existsByBoard_IdAndBoardMember_Id(boardId, collaborator.getId())) {
                throw new IllegalArgumentException("칸반 보드에 이미 협업자로 등록된 사용자입니다.");
            }
            if (collaborator.getId().equals(board.getUser().getId())) {
                throw new DuplicateRequestException("입력하신 아이디는 칸반 보드의 오너입니다.");
            }

            BoardMember boardMember = BoardMemberRequestDto.toEntity(collaborator, board);
            boardMemberRepository.save(boardMember);
        } catch (Exception e) {
            throw new RuntimeException("협업자 등록에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 협업 초대받은 보드 협업자 명단 수정
    @Transactional
    public void updateCollaborator(Board board, BoardMember boardMember, User newCollaborator) {
        try {
            if (!boardMember.getBoard().equals(board)) {
                throw new IllegalArgumentException("해당 칸반 보드의 협업자가 아닙니다.");
            }
            //명단에 내가 이미 있어서 또 초대할 필요가 없는지 확인
            if (boardMember.getBoard().getBoardMembers().stream()
                .anyMatch(user -> user.getCollaborator().equals(newCollaborator))) {
                throw new DuplicateRequestException("이미 협업자로 할당된 사용자입니다.");
            }
            boardMember.updateCollaborator(newCollaborator);
        } catch (Exception e) {
            throw new RuntimeException("협업자 수정에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 협업 초대받은 보드 협업자 삭제
    @Transactional
    public void deleteCollaborator(Long boardId, Long userId) {
        BoardMember boardMember = boardMemberRepository.findByBoard_IdAndCollaborator_Id(boardId, userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 칸반 보드의 협업자가 아닙니다."));
        try {
            boardMemberRepository.delete(boardMember);
        } catch (Exception e) {
            throw new RuntimeException("협업자 삭제에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    // 내 칸반 보드의 협업자 명단 조회
    public BoardMembersResponseDto getBoardMembers(Long id) {
        List<BoardMember> boardList = boardMemberRepository.findByBoard_Id(id);

        return BoardMembersResponseDto.of(boardList);
    }

    public BoardMember findCollaborator(Long boardMemberId) {
        return boardMemberRepository.findById(boardMemberId)
            .orElseThrow(() -> new NotFoundException("해당 칸반 보드에 존재하지 않는 협업자입니다."));
    }*/
}
