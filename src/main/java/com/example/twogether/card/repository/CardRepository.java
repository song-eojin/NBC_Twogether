package com.example.twogether.card.repository;

import com.example.twogether.card.entity.Card;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByDeck_Id(Long deckId);
}
