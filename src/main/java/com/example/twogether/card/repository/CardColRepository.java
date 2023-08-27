package com.example.twogether.card.repository;

import com.example.twogether.card.entity.Card;
import com.example.twogether.card.entity.CardCollaborator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardColRepository extends JpaRepository<CardCollaborator, Long> {

    List<CardCollaborator> findAlLByCard(Card cardId);
    Optional<CardCollaborator> findByCardAndEmail(Card card, String email);
    Optional<CardCollaborator> findByEmail(String email);
    boolean existsByCardAndEmail(Card ourCard, String email);
}
