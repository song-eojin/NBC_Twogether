package com.example.twogether.board.service;

import com.example.twogether.board.dto.BoardRequestDto;
import com.example.twogether.board.dto.BoardResponseDto;
import com.example.twogether.board.dto.BoardsResponseDto;
import com.example.twogether.board.entity.Board;
import com.example.twogether.board.repository.BoardRepository;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.repository.UserRepository;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardUserRepository boardUserRepository;
    private final UserRepository userRepository;

    // 보드 생성
    @Transactional
    public BoardResponseDto createBoard(BoardRequestDto boardRequestDto, User user) {
        try {
            Board board = boardRequestDto.toEntity(user);
            boardRepository.save(board);
            return BoardResponseDto.of(board);
        } catch (Exception e) {
            throw new RuntimeException("칸반 보드 생성에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 전체 조회 (본인이 생성한 보드)
    @Transactional(readOnly = true)
    public BoardsResponseDto getAllBoards(User user) {
        try {
            List<Board> boards = boardRepository.findAllByUserOrderByCreatedAtDesc(user);
            return BoardsResponseDto.of(boards);
        } catch (Exception e) {
            throw new RuntimeException("모든 칸반 보드 조회를 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 단건 조회 (본인이 생성한 보드)
    @Transactional(readOnly = true)
    public BoardResponseDto getBoardById(User user, Long id) {
        try {
            Board board = findBoard(user, id);
            return BoardResponseDto.of(board);
        } catch (Exception e) {
            throw new RuntimeException("칸반 보드 조회를 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 삭제 (본인이 생성한 보드)
    @Transactional
    public void deleteBoard(Board board, User user) {
        try {
            if (!board.getUser().equals(user)) {
                throw new RejectedExecutionException("칸반 보드는 작성자만 삭제 가능합니다.");
            }
            boardRepository.delete(board);
        } catch (Exception e) {
            throw new RuntimeException("칸반 보드 삭제에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 이름 수정
    @Transactional
    public Board updateBoardName(Board board, BoardRequestDto boardRequestDto) {
        try {
            board.updateName(boardRequestDto);
            return board;
        } catch (Exception e) {
            throw new RuntimeException("칸반 보드 이름 수정에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 배경색상 수정
    @Transactional
    public Board updateBoardColor(Board board, BoardRequestDto boardRequestDto) {
        try {
            board.updateColor(boardRequestDto);
            return board;
        } catch (Exception e) {
            throw new RuntimeException("칸반 보드 배경색상 수정에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 설명 수정
    @Transactional
    public Board updateBoardInfo(Board board, BoardRequestDto boardRequestDto) {
        try {
            board.updateInfo(boardRequestDto);
            return board;
        } catch (Exception e) {
            throw new RuntimeException("칸반 보드 설명 수정에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    public Board findBoard(User user, Long id) {
        if (user == null) {
            throw new AuthenticationServiceException("로그인 후 보드를 조회할 수 있습니다.");
        }
        return boardRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 칸반 보드입니다."));
    }


    /*
    협업자 관련


    // 보드 전체 조회 (협업 초대 받은 보드)
    @Transactional(readOnly = true)
    public BoardsResponseDto getCollaboratedBoards(UserDetailsImpl userDetails) {
        try {
            List<BoardUser> boardUsers = boardUserRepository.findByCollaborator(
                userDetails.getUser());

            List<Board> collaboratedBoards = boardUsers.stream()
                .map(BoardUser::getBoard)
                .collect(Collectors.toList());

            return BoardsResponseDto.of(collaboratedBoards);
        } catch (Exception e) {
            throw new RuntimeException("협업 중인 칸반 보드 조회를 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 단건 조회 (협업 초대 받은 보드)
    @Transactional(readOnly = true)
    public BoardResponseDto getCollaboratedBoardById(User user, Long id) {
        try {
            Board board = findBoard(user, id);
            return BoardResponseDto.of(board);
        } catch (Exception e) {
            throw new RuntimeException("협업 중인 칸반 보드 조회를 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 협업자 등록
    // 허락받아야 초대 가능한 로직으로 변경하기 - 추후 작업
    @Transactional
    public void addCollaborator(Long boardId,String username) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보드입니다."));
        System.out.println("check"+username);
        User collaborator = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        try {
            if (boardUserRepository.existsByBoard_IdAndCollaborator_Id(boardId, collaborator.getId())) {
                throw new IllegalArgumentException("칸반 보드에 이미 협업자로 등록된 사용자입니다.");
            }
            if (collaborator.getId().equals(board.getUser().getId())) {
                throw new DuplicateRequestException("입력하신 아이디는 칸반 보드의 오너입니다.");
            }

            BoardUser boardUser = BoardUserRequestDto.toEntity(collaborator, board);
            boardUserRepository.save(boardUser);
        } catch (Exception e) {
            throw new RuntimeException("협업자 등록에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 협업자 명단 수정
    @Transactional
    public void updateCollaborator(Board board, BoardUser boardUser, User newCollaborator) {
        try {
            if (!boardUser.getBoard().equals(board)) {
                throw new IllegalArgumentException("해당 칸반 보드의 협업자가 아닙니다.");
            }
            //명단에 내가 이미 있어서 또 초대할 필요가 없는지 확인
            if (boardUser.getBoard().getBoardUsers().stream()
                .anyMatch(user -> user.getCollaborator().equals(newCollaborator))) {
                throw new DuplicateRequestException("이미 협업자로 할당된 사용자입니다.");
            }
            boardUser.updateCollaborator(newCollaborator);
        } catch (Exception e) {
            throw new RuntimeException("협업자 수정에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    // 보드 협업자 삭제
    @Transactional
    public void deleteCollaborator(Long boardId, Long userId) {
        BoardUser boardUser = boardUserRepository.findByBoard_IdAndCollaborator_Id(boardId, userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 칸반 보드의 협업자가 아닙니다."));
        try {
            boardUserRepository.delete(boardUser);
        } catch (Exception e) {
            throw new RuntimeException("협업자 삭제에 실패했습니다. 이유: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    // 내 칸반 보드의 협업자 명단 조회
    public BoardUsersResponseDto getBoardUsers(Long id) {
        List<BoardUser> boardList = boardUserRepository.findByBoard_Id(id);

        return BoardUsersResponseDto.of(boardList);
    }

    public BoardUser findCollaborator(Long boardUserId) {
        return boardUserRepository.findById(boardUserId)
            .orElseThrow(() -> new NotFoundException("해당 칸반 보드에 존재하지 않는 협업자입니다."));
    }

    */

}
