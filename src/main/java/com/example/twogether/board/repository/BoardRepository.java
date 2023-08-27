package com.example.twogether.board.repository;

import com.example.twogether.board.entity.Board;
import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<List<Board>> findAllByWorkspace(Workspace workspace);
    Optional<Board> findByWorkspaceAndId(Workspace workspace, Long boardId);
    Optional<Board> findByWorkspace_IdAndId(Long workspaceId, Long boardId);
    List<Board> findAllByWorkspace_Id(Long id);
    Optional<Board> findByWorkspace_IdAndIdAndAndBoardCollaborators_Email(Long id, Long boardId, String email);
    List<Board> findAllBoardsByWorkspace_IdAndBoardCollaborators_Email(Long wpId, String email);
    boolean existsByUser(User user);
}
