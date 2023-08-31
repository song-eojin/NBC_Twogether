package com.example.twogether.board.repository;

import com.example.twogether.board.entity.Board;
import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<List<Board>> findAllByWorkspace(Workspace workspace);

    List<Board> findAllByWorkspace_Id(Long id);

    boolean existsByUser(User user);
}
