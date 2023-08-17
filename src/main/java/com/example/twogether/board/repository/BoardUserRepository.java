package com.example.twogether.board.repository;

import com.example.twogether.board.entity.BoardUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardUserRepository extends JpaRepository<BoardUser, Long> {
    List<BoardUser> findAll();
//    List<BoardUser> findByCollaborator(User collaborator);
    List<BoardUser> findByBoard_Id(Long id);

    boolean existsByBoard_IdAndCollaborator_Id(Long boardId, Long CollaboratorId);

    Optional<BoardUser> findByBoard_IdAndCollaborator_Id(Long boardId, Long userId);
}
