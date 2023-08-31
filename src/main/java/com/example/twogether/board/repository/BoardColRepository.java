package com.example.twogether.board.repository;

import com.example.twogether.board.entity.Board;
import com.example.twogether.board.entity.BoardCollaborator;
import com.example.twogether.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardColRepository extends JpaRepository<BoardCollaborator, Long> {

    List<BoardCollaborator> findAllByBoard(Board board);
    Optional<BoardCollaborator> findByBoardAndEmail(Board board, String email);
    boolean existsByEmail(String email);
    boolean existsByBoardAndEmail(Board board, String email);
    void deleteAllByBoard_Id(Long boardId);

    List<BoardCollaborator> findAllByUser_Id(Long id);
}
