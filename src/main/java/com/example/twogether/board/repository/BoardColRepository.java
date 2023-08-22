package com.example.twogether.board.repository;

import com.example.twogether.board.entity.Board;
import com.example.twogether.board.entity.BoardCollaborator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardColRepository extends JpaRepository<BoardCollaborator, Long> {

    List<BoardCollaborator> findByBoard(Board board);
    boolean existsByBoardAndEmail(Board board, String email);
    Optional<BoardCollaborator> findByBoardAndEmail(Board board, String email);
}