package com.example.twogether.workspace.controller;

import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.common.security.UserDetailsImpl;
import com.example.twogether.workspace.dto.WpColRequestDto;
import com.example.twogether.workspace.dto.WpResponseDto;
import com.example.twogether.workspace.dto.WpsResponseDto;
import com.example.twogether.workspace.service.WpColService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "워크스페이스 협업자 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WpColController {

    private final WpColService wpColService;

    @Operation(summary = "워크스페이스 협업자 초대")
    @PostMapping("/workspaces/{wpId}/invite")
    public ResponseEntity<ApiResponseDto> createWpCol(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long wpId,
        @RequestBody WpColRequestDto wpColRequestDto
    ) {
        wpColService.inviteWpCol(userDetails.getUser(), wpId, wpColRequestDto.getEmail());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(),
            "워크스페이스에 협업자를 초대하였습니다."));
    }

    @Operation(summary = "워크스페이스 협업자 추방")
    @DeleteMapping("/workspaces/{wpId}/invite")
    public ResponseEntity<ApiResponseDto> outWpCol(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long wpId,
        @RequestBody WpColRequestDto wpColRequestDto
    ) {
        wpColService.outWpCol(userDetails.getUser(), wpId, wpColRequestDto.getEmail());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(),
            "워크스페이스에서 협업자를 추방하였습니다."));
    }

    @Operation(summary = "초대된 워크스페이스 단일 조회")
    @GetMapping("/workspaces/{wpId}/invite")
    public ResponseEntity<WpResponseDto> getInvitedWp(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long wpId
    ) {

        WpResponseDto invitedWp = wpColService.getWpCol(userDetails.getUser(), wpId);
        return ResponseEntity.status(HttpStatus.OK).body(invitedWp);
    }

    @Operation(summary = "초대된 워크스페이스 전체 조회")
    @GetMapping("/workspaces/invite")
    public ResponseEntity<WpsResponseDto> getInvitedWps(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        WpsResponseDto invitedWps = wpColService.getWpCols(userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK).body(invitedWps);
    }
}