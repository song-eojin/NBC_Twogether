package com.example.twogether.workspace.service;

import com.example.twogether.alarm.event.TriggerEventPublisher;
import com.example.twogether.board.dto.BoardColRequestDto;
import com.example.twogether.board.entity.Board;
import com.example.twogether.board.entity.BoardCollaborator;
import com.example.twogether.board.repository.BoardColRepository;
import com.example.twogether.board.repository.BoardRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.entity.UserRoleEnum;
import com.example.twogether.user.repository.UserRepository;
import com.example.twogether.workspace.dto.WpColRequestDto;
import com.example.twogether.workspace.dto.WpResponseDto;
import com.example.twogether.workspace.dto.WpsResponseDto;
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.entity.WorkspaceCollaborator;
import com.example.twogether.workspace.repository.WpColRepository;
import com.example.twogether.workspace.repository.WpRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WpColService {

    private final WpColRepository wpColRepository;
    private final WpRepository wpRepository;
    private final BoardColRepository boardColRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final TriggerEventPublisher eventPublisher;

    // 워크스페이스 협업자 초대 - 보드 협업자로도 자동 초대
    @Transactional
    public void inviteWpCol(User user, Long wpId, String email) {

        Workspace foundWorkspace = findWpById(wpId);
        checkWorkspacePermissions(foundWorkspace, user, email);

        // workspaceCollaborators 필드를 로드하여 Lazy Loading을 강제로 발생시키기
        foundWorkspace.loadWorkspaceCollaborators();

        // 이미 등록된 사용자 초대당하기 불가
        if (wpColRepository.existsByWorkspaceAndEmail(foundWorkspace, email)) {
            throw new CustomException(CustomErrorCode.WORKSPACE_COLLABORATOR_ALREADY_EXISTS);
        }

        // 워크스페이스 협업자로 등록
        User invitedUser = findUser(email);
        WorkspaceCollaborator newWpCol = WpColRequestDto.toEntity(invitedUser, foundWorkspace);

        // 아이디 수동 할당 - 데이터가 덮어 씌어지는 문제 방지
        newWpCol.assignNewId();

        wpColRepository.save(newWpCol);
        eventPublisher.publishInviteWpColEvent(user, invitedUser, foundWorkspace);

        // 워크스페이스에서 초대한 협업자 모든 하위 보드도 자동 초대
        List<Board> foundAllBoards = findAllBoards(foundWorkspace);
        if (foundAllBoards != null && !foundAllBoards.isEmpty()) {

            for (Board foundBoard : foundAllBoards) {

                // 해당 보드에 이미 등록된 협업자인 경우 예외 던지기
                if (!boardColRepository.existsByBoardAndEmail(foundBoard, invitedUser.getEmail())) {
                    BoardCollaborator boardCollaborator = BoardColRequestDto.toEntity(invitedUser, foundBoard);
                    boardColRepository.save(boardCollaborator);
                }
            }
        }
    }

    @Transactional
    public void autoInviteWpCol(User user, Long wpId, String email) {

        Workspace foundWorkspace = findWpById(wpId);

        // workspaceCollaborators 필드를 로드하여 Lazy Loading을 강제로 발생시키기
        foundWorkspace.loadWorkspaceCollaborators();

        // 이미 등록된 사용자 초대당하기 불가
        if (wpColRepository.existsByWorkspaceAndEmail(foundWorkspace, email)) {
            throw new CustomException(CustomErrorCode.WORKSPACE_COLLABORATOR_ALREADY_EXISTS);
        }

        // 워크스페이스 협업자로 등록
        User invitedUser = findUser(email);
        WorkspaceCollaborator newWpCol = WpColRequestDto.toEntity(invitedUser, foundWorkspace);

        // 아이디 수동 할당 - 데이터가 덮어 씌어지는 문제 방지
        newWpCol.assignNewId();

        wpColRepository.save(newWpCol);
        eventPublisher.publishInviteWpColEvent(user, invitedUser, foundWorkspace);
    }

    // 워크스페이스 협업자 추방
    @Transactional
    public void outWpCol(User user, Long wpId, String email) {

        Workspace foundWorkspace = findWpById(wpId);
        checkWorkspacePermissions(foundWorkspace, user, email);

        User invitedUser = findUser(email);

        // 워크스페이스 협업자 삭제
        WorkspaceCollaborator foundWpCol = findWpColByEmail(foundWorkspace, email);
        wpColRepository.delete(foundWpCol);

        // 워크스페이스에서 추방한 협업자 모든 하위 보드에서 자동 추방
        List<Board> foundAllBoards = findAllBoards(foundWorkspace);
        for (Board foundBoard : foundAllBoards) {

            List<BoardCollaborator> boardCollaborators = boardColRepository.findAllByBoard(foundBoard);
            if (boardCollaborators != null && !boardCollaborators.isEmpty()) {

                // 이미 추방된 보드 협업자
                if (!boardColRepository.existsByBoardAndEmail(foundBoard, email)) {
                    throw new CustomException(CustomErrorCode.BOARD_COLLABORATOR_ALREADY_OUT);
                }

                // 보드 협업자 삭제
                BoardCollaborator foundBoardCol = findBoardCol(foundBoard, invitedUser);
                boardColRepository.delete(foundBoardCol);
            }
        }
    }

    // 초대된 워크스페이스 단건 조회
    @Transactional(readOnly = true)
    public WpResponseDto getWpCol(User user, Long wpId) {

        Workspace foundWorkspace = findInvitedWp(user.getEmail(), wpId);
        return WpResponseDto.of(foundWorkspace);
    }

    // 초대된 워크스페이스 전체 조회
    @Transactional(readOnly = true)
    public WpsResponseDto getWpCols(User user) {

        List<Workspace> AllWorkspaces = findAllWpsByEmail(user.getEmail());
        List<Workspace> invitedWorkspaces = new ArrayList<>();

        // 보드의 협업자 목록에 현재 사용자의 이메일이 포함되어 있다면 보드를 추가
        for (Workspace workspace : AllWorkspaces) {
            invitedWorkspaces.add(workspace.editAllWpAndBoards(user.getEmail()));
        }
        return WpsResponseDto.of(invitedWorkspaces);
    }

    private Workspace findWpById(Long wpId) {

        // return wpRepository.findByIdWithCollaborators(wpId).orElseThrow(() ->
        return wpRepository.findById(wpId).orElseThrow(() ->
            new CustomException(CustomErrorCode.WORKSPACE_NOT_FOUND));
    }

    private Workspace findInvitedWp(String email, Long wpId) {

        return wpRepository.findByIdAndWorkspaceCollaborators_Email(wpId, email).orElseThrow(() ->
            new CustomException(CustomErrorCode.WORKSPACE_NOT_FOUND));
    }

    private List<Workspace> findAllWpsByEmail(String email) {
        return wpRepository.findAllByWorkspaceCollaborators_Email(email);
    }

    private User findUser(String email) {

        return userRepository.findByEmail(email).orElseThrow(() ->
            new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    private List<Board> findAllBoards(Workspace foundWorkspace) {
        return boardRepository.findAllByWorkspace(foundWorkspace).orElseThrow(() ->
            new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
    }

    private WorkspaceCollaborator findWpColByEmail(Workspace foundWorkspace, String email) {

        return wpColRepository.findByWorkspaceAndEmail(foundWorkspace, email).orElseThrow(() ->
            new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    private BoardCollaborator findBoardCol(Board foundBoard, User foundUser) {

        return boardColRepository.findByBoardAndEmail(foundBoard, foundUser.getEmail())
            .orElseThrow(() ->
                new CustomException(CustomErrorCode.BOARD_COLLABORATOR_NOT_FOUND));
    }

    private void checkWorkspacePermissions(Workspace workspace, User user, String email) {
        if (!workspace.getUser().getId().equals(user.getId()) &&
            !user.getRole().equals(UserRoleEnum.ADMIN)) {

            throw new CustomException(CustomErrorCode.NOT_YOUR_WORKSPACE);
        }

        if (email.equals(user.getEmail())) { // 추후 프론트에서 예외처리되면 삭제될 예정

            throw new CustomException(CustomErrorCode.THIS_IS_YOUR_WORKSPACE);
        }
    }
}
