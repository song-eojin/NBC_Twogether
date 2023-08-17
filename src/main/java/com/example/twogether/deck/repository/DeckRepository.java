package com.example.twogether.deck.repository;

import com.example.twogether.deck.entity.Deck;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeckRepository extends JpaRepository<Deck, Long> {

    Optional<Deck> findByParentId(Long parentId);
}
