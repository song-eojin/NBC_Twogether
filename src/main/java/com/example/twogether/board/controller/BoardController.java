package com.example.twogether.board.controller;

import com.example.twogether.board.dto.BoardRequestDto;
import com.example.twogether.board.dto.BoardResponseDto;
import com.example.twogether.board.dto.BoardsResponseDto;
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

@Tag(name = "보드 API")
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {

    private final BoardService boardService;

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
    public ResponseEntity<ApiResponseDto> updateBoard(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id,
        @RequestBody BoardRequestDto boardRequestDto) {
        boardService.updateBoard(userDetails.getUser(), id, boardRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
            .body(new ApiResponseDto(HttpStatus.OK.value(), "칸반 보드가 수정되었습니다."));
    }

    // 보드 삭제
    @Operation(summary = "칸반 보드 삭제")
    @DeleteMapping("/boards/{id}")
    public ResponseEntity<ApiResponseDto> deleteBoard(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        boardService.deleteBoard(userDetails.getUser(), id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new ApiResponseDto(HttpStatus.OK.value(), "칸반 보드 삭제 성공"));
    }
}