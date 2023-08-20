package com.example.twogether.board.repository;

import com.example.twogether.board.entity.BoardMember;
import com.example.twogether.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {

    List<BoardMember> findAllByBoardCollabo(User user);

    boolean existsByBoard_IdAndBoardCollabo_Id(Long boardId, Long id);
}
