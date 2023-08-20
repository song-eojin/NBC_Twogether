package com.example.twogether.workspace.controller;

import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.common.security.UserDetailsImpl;
import com.example.twogether.user.service.UserService;
import com.example.twogether.workspace.dto.WorkspaceRequestDto;
import com.example.twogether.workspace.dto.WorkspaceResponseDto;
import com.example.twogether.workspace.dto.WorkspacesResponseDto;
import com.example.twogether.workspace.service.WorkspaceService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
public class WorkspaceController {

    private final UserService userService;
    private final WorkspaceService workspaceService;
    @Operation(summary = "워크스페이스 생성")
    @PostMapping("/workspaces")
    public ResponseEntity<ApiResponseDto> createWorkspaces(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody WorkspaceRequestDto workspaceRequestDto) {
        workspaceService.createWorkspace(workspaceRequestDto, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "Workspace Creation Success!"));
    }

    @Operation(summary = "워크스페이스 전체 조회")
    @GetMapping("/workspaces")
    public ResponseEntity<WorkspacesResponseDto> getWorkspaces(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        WorkspacesResponseDto workspaces = workspaceService.getAllWorkspaces(userDetails.getUser());
        return ResponseEntity.ok().body(workspaces);
    }

    @Operation(summary = "워크스페이스 단일 조회")
    @GetMapping("/workspaces/{id}")
    public ResponseEntity<WorkspaceResponseDto> getWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        WorkspaceResponseDto workspaceResponseDto = workspaceService.getWorkspace(userDetails.getUser(), id);
        return ResponseEntity.ok().body(workspaceResponseDto);
    }

    @Operation(summary = "워크스페이스 수정")
    @PutMapping("/workspaces/{id}")
    public ResponseEntity<ApiResponseDto> updateWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id, @RequestBody WorkspaceRequestDto workspaceRequestDto) {
        workspaceService.updateWorkspace(userDetails.getUser(), id, workspaceRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "Workspace Update Success!"));
    }

    @Operation(summary = "워크스페이스 삭제")
    @DeleteMapping("/workspaces/{id}")
    public ResponseEntity<ApiResponseDto> deleteWorkspace(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        workspaceService.deleteWorkspace(userDetails.getUser(), id);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "Workspace Delete Success!"));
    }
}
