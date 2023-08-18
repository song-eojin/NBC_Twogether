package com.example.twogether.board.controller;

import com.example.twogether.board.dto.BoardRequestDto;
import com.example.twogether.board.dto.BoardResponseDto;
import com.example.twogether.board.dto.BoardsResponseDto;
import com.example.twogether.board.entity.Board;
import com.example.twogether.board.service.BoardService;
import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.common.security.UserDetailsImpl;
import com.example.twogether.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Board Example API", description = "칸반 보드 API")
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;
    private final UserService userService;

    // 보드 생성
    @Operation(summary = "칸반 보드 생성")
    @PostMapping("/boards")
    public ResponseEntity<BoardResponseDto> createBoard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody BoardRequestDto boardRequestDto) {
        BoardResponseDto result = boardService.createBoard(boardRequestDto, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 보드 전체 조회 - Test 용
    @Operation(summary = "칸반 보드 전체 조회")
    @GetMapping("/boards")
    public ResponseEntity<BoardsResponseDto> getAllBoards(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardsResponseDto boards = boardService.getAllBoards(userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(boards);
    }

    // 보드 단건 조회
    @Operation(summary = "칸반 보드 단건 조회")
    @GetMapping("/boards/{id}")
    public ResponseEntity<BoardResponseDto> getBoardById(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        BoardResponseDto result = boardService.getBoardById(userDetails.getUser(), id);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 보드 수정
    @Operation(summary = "칸반 보드 수정", description = "")
    @PatchMapping("/boards/{id}")
    public ResponseEntity<ApiResponseDto> updateBoard(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody BoardRequestDto boardRequestDto) {
        Board board = boardService.findBoard(userDetails.getUser(), id);
        boardService.updateBoard(board, boardRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(HttpStatus.OK.value(), "칸반 보드가 수정되었습니다."));
    }

    // 보드 삭제
    @Operation(summary = "칸반 보드 삭제")
    @DeleteMapping("/boards/{id}")
    public ResponseEntity<ApiResponseDto> deleteBoard(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        Board board = boardService.findBoard(userDetails.getUser(), id);
        boardService.deleteBoard(board, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto(HttpStatus.OK.value(), "칸반 보드 삭제 성공"));
    }


    /*
    협업자 관련


    // 보드 전체 조회 (협업 초대 받은 보드)
    @Operation(summary = "get collaborator's boards", description = "협업하고 있는 칸반 보드 전체 조회")
    @GetMapping("/boards/collaborators")
    public ResponseEntity<BoardsResponseDto> getCollaboratedBoards(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardsResponseDto result = boardService.getCollaboratedBoards(userDetails);
        return ResponseEntity.ok().body(result);
    }

    // 보드 단건 조회 (협업 초대 받은 보드)
    @Operation(summary = "get collaborator's board by id", description = "협업하고 있는 칸반 보드 단건 조회")
    @GetMapping("/boards/collaborators/{id}")
    public ResponseEntity<BoardResponseDto> getCollaboratedBoardById(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        BoardResponseDto result = boardService.getCollaboratedBoardById(userDetails.getUser(), id);
        return ResponseEntity.ok().body(result);
    }

    // 보드 협업자 등록
    @Operation(summary = "add Collaborators of Board", description = "칸반 보드에 협업자 등록")
    @PostMapping("/boards/{boardId}/collaborators")
    public ResponseEntity<ApiResponseDto> addCollaborator(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long boardId,@RequestBody BoardCollaboRequestDto requestDto) {

        boardService.addCollaborator(boardId,requestDto.getUsername());

        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "칸반 보드에 협업자가 등록되었습니다."));
    }

    // 보드 협업자 명단 수정
    @Operation(summary = "update Collaborators of Board", description = "칸반 보드의 협업자 명단 수정")
    @PutMapping("/boards/collaborators/{boardId}/{boardUserId}")
    public ResponseEntity<ApiResponseDto> updateCollaborator(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId,
        @PathVariable Long boardUserId
    ) {
        Board board = boardService.findBoard(userDetails.getUser(), boardId);
        BoardUser boardUser = boardService.findCollaborator(boardUserId);
        User newCollaborator = userService.findUserByUserid(userDetails.getUser().getId());

        boardService.updateCollaborator(board, boardUser, newCollaborator);
        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "칸반 보드의 협업자가 수정되었습니다."));
    }

    // 보드 협업자 조회
    @GetMapping("/boards/get-collaborators/{boardId}")
    public ResponseEntity<BoardUsersResponseDto> getBoardUsers(@PathVariable Long boardId) {
        BoardUsersResponseDto boardUser = boardService.getBoardUsers(boardId);

        return ResponseEntity.ok().body(boardUser);
    }

    // 보드 협업자 삭제
    @Operation(summary = "update Collaborators of Board", description = "칸반 보드의 협업자 명단 수정")
    @DeleteMapping("/boards/collaborators/{boardId}/{userId}")
    public ResponseEntity<ApiResponseDto> deleteCollaborator(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId,
        @PathVariable Long userId
    ) {
        boardService.deleteCollaborator(boardId, userId);
        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "칸반 보드의 협업자가 삭제되었습니다."));
    }
    */
}