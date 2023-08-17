package com.example.twogether.board.controller;

import com.example.twogether.board.service.BoardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Board Example API", description = "칸반 보드 API")
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {
    private final BoardService boardService;

    // 보드 생성
//    @Operation(summary = "create board", description = "칸반 보드 생성")
//    @PostMapping("/boards")
//    public ResponseEntity<BoardResponseDto> createBoard(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
//        @RequestBody BoardRequestDto boardRequestDto) {
//        BoardResponseDto result = boardService.createBoard(boardRequestDto, userDetails.getUser());
//        return ResponseEntity.status(HttpStatus.CREATED).body(result);
//    }
//
//    // 보드 전체 조회 (본인이 생성한 보드)
//    @Operation(summary = "get all boards", description = "모든 칸반 보드 조회")
//    @GetMapping("/boards")
//    public ResponseEntity<BoardsResponseDto> getAllBoards(
//        @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        BoardsResponseDto boards = boardService.getAllBoards(userDetails.getUser());
//        return ResponseEntity.ok().body(boards);
//    }
//
//    // 보드 단건 조회 (본인이 생성한 보드)
//    @Operation(summary = "get owner's board by id", description = "소유한 칸반 보드 단건 조회")
//    @GetMapping("/boards/{id}")
//    public ResponseEntity<BoardResponseDto> getBoardById(
//        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
//        BoardResponseDto result = boardService.getBoardById(userDetails.getUser(), id);
//        return ResponseEntity.ok().body(result);
//    }
//
//    // 보드 삭제 (본인이 생성한 보드)
//    @Operation(summary = "delete Board", description = "칸반 보드 삭제")
//    @DeleteMapping("/boards/{id}")
//    public ResponseEntity<ApiResponseDto> deleteBoard(
//        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
//        Board board = boardService.findBoard(userDetails.getUser(), id);
//        boardService.deleteBoard(board, userDetails.getUser());
//        return ResponseEntity.ok().body(new ApiResponseDto("칸반 보드 삭제 성공", HttpStatus.OK.value()));
//    }
//
//    // 보드 이름 수정
//    @Operation(summary = "update Board's Name", description = "칸반 보드 이름 수정")
//    @PutMapping("/boards/names/{id}")
//    public ResponseEntity<ApiResponseDto> updateBoardName(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
//        @PathVariable Long id,
//        @RequestBody BoardRequestDto boardRequestDto
//    ) {
//        Board board = boardService.findBoard(userDetails.getUser(), id);
//        boardService.updateBoardName(board, boardRequestDto);
//        return ResponseEntity.ok()
//            .body(new ApiResponseDto("칸반 보드의 이름이 수정되었습니다.", HttpStatus.OK.value()));
//    }
//
//    // 보드 배경색상 수정
//    @Operation(summary = "update Board's Color", description = "칸반 보드 배경색상 수정")
//    @PutMapping("/boards/colors/{id}")
//    public ResponseEntity<ApiResponseDto> updateBoardColor(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
//        @PathVariable Long id,
//        @RequestBody BoardRequestDto boardRequestDto
//    ) {
//        Board board = boardService.findBoard(userDetails.getUser(), id);
//        boardService.updateBoardColor(board, boardRequestDto);
//        return ResponseEntity.ok()
//            .body(new ApiResponseDto("칸반 보드의 색상이 수정되었습니다.", HttpStatus.OK.value()));
//    }
//
//    // 보드 설명 수정
//    @Operation(summary = "update Board's Info", description = "칸반 보드 설명 수정")
//    @PutMapping("/boards/infos/{id}")
//    public ResponseEntity<ApiResponseDto> updateBoardInfo(
//        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id,
//        @RequestBody BoardRequestDto boardRequestDto) {
//        Board board = boardService.findBoard(userDetails.getUser(), id);
//        boardService.updateBoardInfo(board, boardRequestDto);
//        return ResponseEntity.ok()
//            .body(new ApiResponseDto("칸반 보드의 설명이 수정되었습니다.", HttpStatus.OK.value()));
//    }
//
//    // 보드 전체 조회 (협업 초대 받은 보드)
//    @Operation(summary = "get collaborator's boards", description = "협업하고 있는 칸반 보드 전체 조회")
//    @GetMapping("/boards/collaborators")
//    public ResponseEntity<BoardsResponseDto> getCollaboratedBoards(
//        @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        BoardsResponseDto result = boardService.getCollaboratedBoards(userDetails);
//        return ResponseEntity.ok().body(result);
//    }
//
//    // 보드 단건 조회 (협업 초대 받은 보드)
//    @Operation(summary = "get collaborator's board by id", description = "협업하고 있는 칸반 보드 단건 조회")
//    @GetMapping("/boards/collaborators/{id}")
//    public ResponseEntity<BoardResponseDto> getCollaboratedBoardById(
//        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
//        BoardResponseDto result = boardService.getCollaboratedBoardById(userDetails.getUser(), id);
//        return ResponseEntity.ok().body(result);
//    }
//
//
//    // 보드 협업자 등록
//    // 허락받아야 초대되는 로직으로 변경하기 - 추후 작업
//    @Operation(summary = "add Collaborators of Board", description = "칸반 보드에 협업자 등록")
//    @PostMapping("/boards/{boardId}/collaborators")
//    public ResponseEntity<ApiResponseDto> addCollaborator(
//        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long boardId,@RequestBody BoardCollaboRequestDto requestDto) {
//
//        boardService.addCollaborator(boardId,requestDto.getUsername());
//
//        return ResponseEntity.ok()
//            .body(new ApiResponseDto("칸반 보드에 협업자가 등록되었습니다.", HttpStatus.OK.value()));
//    }
//
//    // 보드 협업자 명단 수정
//    @Operation(summary = "update Collaborators of Board", description = "칸반 보드의 협업자 명단 수정")
//    @PutMapping("/boards/collaborators/{boardId}/{boardUserId}")
//    public ResponseEntity<ApiResponseDto> updateCollaborator(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
//        @PathVariable Long boardId,
//        @PathVariable Long boardUserId
//    ) {
//        Board board = boardService.findBoard(userDetails.getUser(), boardId);
//        BoardUser boardUser = boardService.findCollaborator(boardUserId);
//        User newCollaborator = userService.findUserByUserid(userDetails.getUser().getId());
//
//        boardService.updateCollaborator(board, boardUser, newCollaborator);
//        return ResponseEntity.ok()
//            .body(new ApiResponseDto("칸반 보드의 협업자가 수정되었습니다.", HttpStatus.OK.value()));
//    }
//
//    // 보드 협업자 조회 (카드 내 Members - Board members 와 동일)
//    @GetMapping("/boards/get-collaborators/{boardId}")
//    public ResponseEntity<BoardUsersResponseDto> getBoardUsers(@PathVariable Long boardId) {
//        BoardUsersResponseDto boardUser = boardService.getBoardUsers(boardId);
//
//        return ResponseEntity.ok().body(boardUser);
//    }
//
//    // 보드 협업자 삭제
//    @Operation(summary = "update Collaborators of Board", description = "칸반 보드의 협업자 명단 수정")
//    @DeleteMapping("/boards/collaborators/{boardId}/{userId}")
//    public ResponseEntity<ApiResponseDto> deleteCollaborator(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
//        @PathVariable Long boardId,
//        @PathVariable Long userId
//    ) {
//        boardService.deleteCollaborator(boardId, userId);
//        return ResponseEntity.ok()
//            .body(new ApiResponseDto("칸반 보드의 협업자가 삭제되었습니다.", HttpStatus.OK.value()));
//    }
}