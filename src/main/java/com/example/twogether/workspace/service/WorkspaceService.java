package com.example.twogether.workspace.service;


import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.entity.UserRoleEnum;
import com.example.twogether.user.repository.UserRepository;
import com.example.twogether.workspace.dto.WorkspaceRequestDto;
import com.example.twogether.workspace.dto.WorkspaceResponseDto;
import com.example.twogether.workspace.dto.WorkspacesResponseDto;
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.repository.WorkspaceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    @Transactional
    public WorkspaceResponseDto createWorkspace(WorkspaceRequestDto workspaceRequestDto, User user){
        Workspace workspace = workspaceRequestDto.toEntity(user);
        workspaceRepository.save(workspace);
        return WorkspaceResponseDto.of(workspace);
    }

    @Transactional(readOnly = true) // 조회용 메서드에 붙임
     public WorkspacesResponseDto getAllWorkspaces(User user) {
         List<Workspace> workspaces = workspaceRepository.findAllByUserOrderByCreatedAtDesc(user);
         return WorkspacesResponseDto.of(workspaces);
     }

    @Transactional(readOnly = true) // 조회용 메서드에 붙임
    public WorkspaceResponseDto getWorkspace(User user, Long Id) {
        Workspace workspace = findWorkspace(user, Id);
        return WorkspaceResponseDto.of(workspace);
    }

    @Transactional
    public WorkspaceResponseDto updateWorkspace(User user, Long id, WorkspaceRequestDto workspaceRequestDto) {
        Workspace workspace = findWorkspace(user, id);
        if(workspace.getUser().getId().equals(user.getId())||user.getRole().equals(UserRoleEnum.ADMIN)) {
            workspace.update(workspaceRequestDto);
            return WorkspaceResponseDto.of(workspace);
        } else throw new CustomException(CustomErrorCode.WORKSPACE_NOT_USER);
    }

    @Transactional
    public void deleteWorkspace(User user, Long id) {
        Workspace workspace = findWorkspace(user, id);
        if(workspace.getUser().getId().equals(user.getId())||user.getRole().equals(UserRoleEnum.ADMIN)) {
            workspaceRepository.delete(workspace);
            // 워크스페이스, 보드, 카드, 멤버 등 삭제
        } else throw new CustomException(CustomErrorCode.WORKSPACE_NOT_USER);
    }

    private Workspace findWorkspace(User user, Long workspaceId) {
        return workspaceRepository.findById(workspaceId).orElseThrow(() ->
            new CustomException(CustomErrorCode.WORKSPACE_NOT_FOUND));
    }
}
