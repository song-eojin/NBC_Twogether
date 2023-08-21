package com.example.twogether.board.controller;

import com.example.twogether.board.dto.BoardColRequestDto;
import com.example.twogether.board.service.BoardColService;
import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.common.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "보드 협업자 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardColController {

    private final BoardColService boardColService;

    // 허락받아야 초대되는 로직으로 develop 할지 고민 중
    @Operation(summary = "칸반 보드에 협업자 초대")
    @PostMapping("/workspaces/{wpId}/boards/{boardId}/invite")
    public ResponseEntity<ApiResponseDto> inviteBoardCol(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long wpId,
        @PathVariable Long boardId,
        @RequestBody BoardColRequestDto boardColRequestDto
    ) {
        boardColService.inviteBoardCol(userDetails.getUser(), wpId, boardId, boardColRequestDto.getEmail());

        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "보드에 협업자가 등록되었습니다."));
    }

    @Operation(summary = "칸반 보드에서 협업자 추방")
    @DeleteMapping("/workspaces/{wpId}/boards/{boardId}/invite")
    public ResponseEntity<ApiResponseDto> outBoardCol(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long wpId,
        @PathVariable Long boardId,
        @RequestBody BoardColRequestDto boardColRequestDto
    ) {
        boardColService.outBoardCol(userDetails.getUser(), wpId, boardId, boardColRequestDto.getEmail());
        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "보드에서 협업자를 추방하였습니다."));
    }
}
