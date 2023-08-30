package com.example.twogether.deck.repository;

import com.example.twogether.deck.entity.Deck;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    List<Deck> findAllByBoard_Id(Long boardId);
}
