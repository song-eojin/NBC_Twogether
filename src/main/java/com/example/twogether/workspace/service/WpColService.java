package com.example.twogether.workspace.service;

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
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.entity.WorkspaceCollaborator;
import com.example.twogether.workspace.repository.WpColRepository;
import com.example.twogether.workspace.repository.WpRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WpColService {

    private final WpColRepository wpColRepository;
    private final WpRepository wpRepository;
    private final BoardColRepository boardColRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 워크스페이스 협업자 초대 - 보드 협업자로도 자동 초대
    @Transactional
    public void inviteWpCol(User user, Long wpId, String email) {

        Workspace foundWorkspace = findWorkspace(wpId); // 중복되는 코드 처리 고민 중

        // 워크스페이스를 생성한 사람만 협업자 초대 가능
        if (!foundWorkspace.getUser().getId().equals(user.getId()) || !user.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(CustomErrorCode.NOT_YOUR_WORKSPACE);
        }

        // 워크스페이스 오너는 초대당하기 불가 - 해당 사항에 대해 추후 프론트에서 예외처리되면 삭제될 예정
        if (email.equals(user.getEmail())) {
            throw new CustomException(CustomErrorCode.THIS_IS_YOUR_WORKSPACE);
        }

        // 이미 등록된 사용자 초대당하기 불가
        if (wpColRepository.existsByWorkspaceAndEmail(foundWorkspace, email)) {
            throw new CustomException(CustomErrorCode.WORKSPACE_COLLABORATOR_ALREADY_EXISTS);
        }

        // 워크스페이스 협업자로 등록
        User foundUser = findUser(email);
        WorkspaceCollaborator foundWpCol = WpColRequestDto.toEntity(foundUser, foundWorkspace);
        wpColRepository.save(foundWpCol);

        // 워크스페이스에서 초대한 협업자 모든 하위 보드도 자동 초대
        List<Board> foundAllBoards = findAllBoards(foundWorkspace);
        if (foundAllBoards != null && !foundAllBoards.isEmpty()) {

            for (Board foundBoard : foundAllBoards) {

                // 해당 보드에 이미 등록된 협업자인 경우 예외 던지기
                if (boardColRepository.existsByBoardAndEmail(foundBoard,
                    foundUser.getEmail())) {
                    log.error("워크스페이스에 포함된 보드 중 이미 협업자가 등록된 경우가 있습니다.");
                    continue;
                }

                BoardCollaborator boardCollaborator = BoardColRequestDto.toEntity(foundUser,
                    foundBoard);
                boardColRepository.save(boardCollaborator); // 수정된 사항 확인에 대해 포스트맨 테스트 필요
            }
        }
    }

    // 워크스페이스 협업자 추방
    @Transactional
    public void outWpCol(User user, Long wpId, String email) {

        Workspace foundWorkspace = findWorkspace(wpId);

        // 워크스페이스를 생성한 사람만 협업자 추방하기 가능
        if (!foundWorkspace.getUser().getId().equals(user.getId()) || !user.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(CustomErrorCode.NOT_YOUR_WORKSPACE);
        }

        // 워크스페이스 오너는 추방당하기 불가 - 해당 사항에 대해 추후 프론트에서 예외처리되면 삭제될 예정
        if (email.equals(user.getEmail())) {
            throw new CustomException(CustomErrorCode.THIS_IS_YOUR_WORKSPACE);
        }

        // 워크스페이스 협업자 삭제
        User foundUser = findUser(email);
        WorkspaceCollaborator foundWpCol = findWpCol(foundWorkspace, email);
        wpColRepository.delete(foundWpCol);

        // 워크스페이스에서 추방한 협업자 모든 하위 보드에서 자동 추방
        List<Board> foundAllBoards = findAllBoards(foundWorkspace);
        for (Board foundBoard : foundAllBoards) {

            List<BoardCollaborator> boardCollaborators = boardColRepository.findByBoard(foundBoard);

            if (boardCollaborators != null && !boardCollaborators.isEmpty()) {
                // 이미 추방된 보드 협업자
                if (!boardColRepository.existsByBoardAndEmail(foundBoard, email)) {
                    throw new CustomException(CustomErrorCode.BOARD_COLLABORATOR_ALREADY_OUT);
                }

                // 보드 협업자 삭제
                BoardCollaborator foundBoardCol = findBoardCol(foundBoard, foundUser);
                boardColRepository.delete(foundBoardCol);
            }
        }
    }

    /*
    @Transactional(readOnly = true)
    public WpsResponseDto getWpCols(User user, WpColRequestDto wpColRequestDto) {

        List<WorkspaceCollaborator> wpCols = wpColRepository.findAllById(Collections.singleton(wpColRequestDto.getId()));
        List<Workspace> foundWorkspaces = new ArrayList<>();
        for(WorkspaceCollaborator workspaceCollaborator : wpCols) {
            Workspace foundWorkspace = findWorkspace(workspaceCollaborator.getId());
            foundWorkspaces.add(foundWorkspace);
        }

        return WpsResponseDto.of(foundWorkspaces);
    }
     */

    private Workspace findWorkspace(Long wpId) {

        return wpRepository.findById(wpId).orElseThrow(() ->
            new CustomException(CustomErrorCode.WORKSPACE_NOT_FOUND));
    }

    private User findUser(String email) {

        return userRepository.findByEmail(email).orElseThrow(() ->
            new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    private List<Board> findAllBoards(Workspace foundWorkspace) {
        return boardRepository.findAllByWorkspace(foundWorkspace).orElseThrow(() ->
            new CustomException(CustomErrorCode.BOARD_NOT_FOUND));
    }

    private WorkspaceCollaborator findWpCol(Workspace foundWorkspace, String email) {

        return wpColRepository.findByWorkspaceAndEmail(foundWorkspace, email).orElseThrow(() ->
            new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    private BoardCollaborator findBoardCol(Board foundBoard, User foundUser) {

        return boardColRepository.findByBoardAndEmail(foundBoard, foundUser.getEmail())
            .orElseThrow(() ->
                new CustomException(CustomErrorCode.BOARD_COLLABORATOR_NOT_FOUND));
    }
}
