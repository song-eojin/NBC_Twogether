package com.example.twogether.workspace.service;


import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.entity.UserRoleEnum;
import com.example.twogether.user.repository.UserRepository;
import com.example.twogether.workspace.dto.WpRequestDto;
import com.example.twogether.workspace.dto.WpResponseDto;
import com.example.twogether.workspace.dto.WpsResponseDto;
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.repository.WpRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class WpService {

    private final WpRepository wpRepository;

    @Transactional
    public WpResponseDto createWorkspace(User wpAuthor, WpRequestDto wpRequestDto){

        Workspace foundWp = wpRequestDto.toEntity(wpAuthor);
        wpRepository.save(foundWp);
        return WpResponseDto.of(foundWp);
    }

    @Transactional
    public WpResponseDto editWorkspace(User wpAuthor, Long id, WpRequestDto wpRequestDto) {

        Workspace workspace = findWorkspace(id);
        if(workspace.getUser().getId().equals(wpAuthor.getId())||wpAuthor.getRole().equals(UserRoleEnum.ADMIN)) {
            workspace.update(wpRequestDto);
            return WpResponseDto.of(workspace);
        } else throw new CustomException(CustomErrorCode.NOT_YOUR_WORKSPACE);
    }

    @Transactional
    public void deleteWorkspace(User wpAuthor, Long id) {

        Workspace workspace = findWorkspace(id);
        if(workspace.getUser().getId().equals(wpAuthor.getId())||wpAuthor.getRole().equals(UserRoleEnum.ADMIN)) {

            wpRepository.delete(workspace);
            /* 워크스페이스, 보드, 카드, 멤버 등 삭제 */

        } else throw new CustomException(CustomErrorCode.NOT_YOUR_WORKSPACE);
    }

    @Transactional(readOnly = true)
    public WpResponseDto getWorkspace(User wpAuthor, Long Id) {

        Workspace workspace = findWorkspace(Id);
        return WpResponseDto.of(workspace);
    }

    @Transactional(readOnly = true)
    public WpsResponseDto getWorkspaces(User wpAuthor) {

        List<Workspace> workspaces = wpRepository.findAllByUserOrderByCreatedAtDesc(wpAuthor);
        return WpsResponseDto.of(workspaces);
    }

    private Workspace findWorkspace(Long workspaceId) {

        return wpRepository.findById(workspaceId).orElseThrow(() ->
            new CustomException(CustomErrorCode.WORKSPACE_NOT_FOUND));
    }
}
