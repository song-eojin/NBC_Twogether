package com.example.twogether.Card.repository;

import com.example.twogether.Card.entity.Card;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByDeck_Id(Long deckId);
}
