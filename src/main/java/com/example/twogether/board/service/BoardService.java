package com.example.twogether.board.service;

import com.example.twogether.board.dto.BoardRequestDto;
import com.example.twogether.board.dto.BoardResponseDto;
import com.example.twogether.board.dto.BoardsResponseDto;
import com.example.twogether.board.entity.Board;
import com.example.twogether.board.entity.BoardMember;
import com.example.twogether.board.repository.BoardMemberRepository;
import com.example.twogether.board.repository.BoardRepository;
import com.example.twogether.common.security.UserDetailsImpl;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.repository.UserRepository;
import com.sun.jdi.request.DuplicateRequestException;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRepository userRepository;

    // 보드 생성
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto boardRequestDto, User boardAuthor) {
        try {
            if (boardAuthor == null) {
                throw new IllegalArgumentException("보드 작성자 정보가 없습니다.");
            }

            Board board = boardRequestDto.toEntity(boardAuthor);
            boardRepository.save(board);
            return BoardResponseDto.of(board);
        } catch (Exception e) {
            log.error("칸반 보드 생성에 실패했습니다. 이유: ", e.getMessage(), e);
            throw new RuntimeException("칸반 보드 생성에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 전체 조회 - Test 용
    @Transactional(readOnly = true)
    public BoardsResponseDto getAllBoards(User boardAuthor) {
        try {
            List<Board> boards = boardRepository.findAllByBoardAuthorOrderByCreatedAtDesc(boardAuthor);
            return BoardsResponseDto.of(boards);
        } catch (Exception e) {
            log.error("칸반 보드 전체 조회를 실패했습니다. 이유: ", e.getMessage(), e);
            throw new RuntimeException("모든 칸반 보드 조회를 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 단건 조회
    @Transactional(readOnly = true)
    public BoardResponseDto getBoardById(User boardAuthor, Long id) {
        try {
            Board board = findBoard(boardAuthor, id);
            return BoardResponseDto.of(board);
        } catch (Exception e) {
            log.error("칸반 보드 단건 조회를 실패했습니다. 이유: ", e.getMessage(), e);
            throw new RuntimeException("칸반 보드 조회를 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 수정
    @Transactional
    public Board updateBoard(User boardAuthor, Long id, BoardRequestDto boardRequestDto) {
        try {
            Board board = findBoard(boardAuthor, id);
            if(boardRequestDto.getTitle()!=null) board.updateTitle(boardRequestDto);
            if(boardRequestDto.getColor()!=null) board.updateColor(boardRequestDto);
            if(boardRequestDto.getInfo()!=null) board.updateInfo(boardRequestDto);
            return board;
        } catch (Exception e) {
            log.error("칸반 보드 수정에 실패했습니다. 이유: ", e.getMessage(), e);
            throw new RuntimeException("칸반 보드 수정에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 삭제
    @Transactional
    public void deleteBoard(User boardAuthor, Long id) {
        try {
            Board board = findBoard(boardAuthor, id);
            if (!board.getBoardAuthor().getEmail().equals(boardAuthor.getEmail())) {
                throw new RejectedExecutionException("칸반 보드는 작성자만 삭제 가능합니다.");
            }
            boardRepository.delete(board);
        } catch (Exception e) {
            log.error("칸반 보드 삭제에 실패했습니다. 이유: ", e.getMessage(), e);
            throw new RuntimeException("칸반 보드 삭제에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    private Board findBoard(User boardAuthor, Long id) {
        if (boardAuthor == null) {
            throw new AuthenticationServiceException("로그인 후 보드를 조회할 수 있습니다.");
        }
        return boardRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 칸반 보드입니다."));
    }
}
