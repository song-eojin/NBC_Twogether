package com.example.twogether.board.repository;

import com.example.twogether.board.entity.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByAuthorOrderByCreatedAtDesc(User author);
}
