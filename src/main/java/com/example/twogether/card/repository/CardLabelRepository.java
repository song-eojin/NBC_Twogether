package com.example.twogether.card.repository;

import com.example.twogether.card.entity.CardLabel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardLabelRepository extends JpaRepository<CardLabel, Long> {

    boolean existsByCard_IdAndLabel_Id(Long cardId, Long labelId);

    Optional<CardLabel> findByCard_IdAndLabel_Id(Long cardId, Long labelId);

    List<CardLabel> findAllByLabel_Id(Long labelId);

    void deleteAllByCard_Id(Long id);

    void deleteAllByLabel_Id(Long labelId);
}
