package com.example.twogether.workspace.service;

import com.example.twogether.user.repository.UserRepository;
import com.example.twogether.workspace.entity.WorkspaceMember;
import com.example.twogether.workspace.repository.WorkspaceMemberRepository;
import com.example.twogether.workspace.repository.WorkspaceRepository;
import org.springframework.transaction.annotation.Transactional;

// public class WorkspaceMemberService {
//    private final WorkspaceMemberRepository workspaceMemberRepository;
//    private final BoardMemberRepository boardMemberRepository;
//    private final UserRepository userRepository;
//
//    // 워크스페이스 작업자 추방
//    @Transactional
//    public void outWorkCollabo(Long workspaceId, Long userId) {
//
//        WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspace_IdAndUser_Id(workspaceId, userId).orElseThrow(() -> new IllegalArgumentException("해당 워크스페이스의 협업자가 아닙니다."));
//
//        try {
//            workspaceMemberRepository.delete(workspaceMember);
//            boardMemberRepository.delete(workspaceMember);
//        } catch (Exception e) {
//            throw new RuntimeException("워크스페이스 협업자 삭제에 실패했습니다. 이유: " + e.getMessage(), e);
//        }
//
//        BoardMember boardUser = boardMemberRepository.findByBoard_IdAndCollaborator_Id(boardId, userId).orElseThrow(() -> new IllegalArgumentException("해당 칸반 보드의 협업자가 아닙니다."));
//
//        try {
//            boardMemberRepository.delete(boardMember);
//        } catch (Exception e) {
//            throw new RuntimeException("보드 협업자 삭제에 실패했습니다. 이유: " + e.getMessage(), e);
//        }
//}
