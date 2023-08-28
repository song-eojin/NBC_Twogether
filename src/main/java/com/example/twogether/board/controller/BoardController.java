package com.example.twogether.board.controller;

import com.example.twogether.board.dto.BoardRequestDto;
import com.example.twogether.board.dto.BoardResponseDto;
import com.example.twogether.board.service.BoardService;
import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.common.security.UserDetailsImpl;
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

@Tag(name = "보드 CRUD API")
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {

    private final BoardService boardService;

    // 보드 생성
    @Operation(summary = "칸반 보드 생성")
    @PostMapping("/workspaces/{wpId}/boards")
    public ResponseEntity<ApiResponseDto> createBoard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long wpId,
        @RequestBody BoardRequestDto boardRequestDto
    ) {

        boardService.createBoard(userDetails.getUser(), wpId, boardRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.CREATED.value(), "보드가 생성되었습니다."));
    }

    // 보드 수정
    @Operation(summary = "칸반 보드 수정")
    @PatchMapping("/boards/{boardId}")
    public ResponseEntity<ApiResponseDto> editBoard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId,
        @RequestBody BoardRequestDto boardRequestDto
    ) {

        boardService.editBoard(userDetails.getUser(), boardId, boardRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new ApiResponseDto(HttpStatus.OK.value(), "보드가 수정되었습니다."));
    }

    // 보드 삭제
    @Operation(summary = "칸반 보드 삭제")
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<ApiResponseDto> deleteBoard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId
    ) {

        boardService.deleteBoard(userDetails.getUser(), boardId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(new ApiResponseDto(HttpStatus.OK.value(), "보드가 삭제되었습니다."));
    }

    // 보드 단일 조회
    @Operation(summary = "칸반 보드 단일 조회")
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId
    ) {

        BoardResponseDto result = boardService.getBoard(userDetails.getUser(), boardId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}