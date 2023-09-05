package com.example.twogether.workspace.controller;

import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.common.security.UserDetailsImpl;
import com.example.twogether.workspace.dto.WpRequestDto;
import com.example.twogether.workspace.dto.WpResponseDto;
import com.example.twogether.workspace.dto.WpsResponseDto;
import com.example.twogether.workspace.service.WpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "워크스페이스 API", description = "워크스페이스 API 정보")
public class WpController {

    private final WpService wpService;

    @Operation(summary = "워크스페이스 생성")
    @PostMapping("/workspaces")
    public ResponseEntity<ApiResponseDto> createWorkspaces(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody WpRequestDto wpRequestDto
    ) {

        wpService.createWorkspace(userDetails.getUser(), wpRequestDto);

        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.CREATED.value(), "워크스페이스가 생성되었습니다."));
    }

    @Operation(summary = "워크스페이스 수정")
    @PatchMapping("/workspaces/{id}")
    public ResponseEntity<ApiResponseDto> editWorkspace(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long id,
        @RequestBody WpRequestDto wpRequestDto
    ) {

        wpService.editWorkspace(userDetails.getUser(), id, wpRequestDto);
        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "워크스페이스가 수정되었습니다."));
    }

    @Operation(summary = "워크스페이스 삭제")
    @DeleteMapping("/workspaces/{id}")
    public ResponseEntity<ApiResponseDto> deleteWorkspace(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long id
    ) {

        wpService.deleteWorkspace(userDetails.getUser(), id);
        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "워크스페이스가 삭제되었습니다."));
    }

    @Operation(summary = "워크스페이스 단일 조회")
    @GetMapping("/workspaces/{id}")
    public ResponseEntity<WpResponseDto> getWorkspace(
        @PathVariable Long id
    ) {

        WpResponseDto wpResponseDto = wpService.getWorkspace(id);
        return ResponseEntity.ok().body(wpResponseDto);
    }

    @Operation(summary = "워크스페이스 전체 조회")
    @GetMapping("/workspaces")
    public ResponseEntity<WpsResponseDto> getWorkspaces(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        WpsResponseDto workspaces = wpService.getWorkspaces(userDetails.getUser());
        return ResponseEntity.ok().body(workspaces);
    }
}
