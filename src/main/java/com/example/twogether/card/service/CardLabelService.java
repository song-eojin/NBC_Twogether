package com.example.twogether.card.service;

import com.example.twogether.card.dto.CardResponseDto;
import com.example.twogether.card.entity.Card;
import com.example.twogether.card.entity.CardLabel;
import com.example.twogether.card.repository.CardLabelRepository;
import com.example.twogether.card.repository.CardRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.label.entity.Label;
import com.example.twogether.label.repository.LabelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardLabelService {

    private final CardRepository cardRepository;
    private final LabelRepository labelRepository;
    private final CardLabelRepository cardLabelRepository;

    @Transactional
    public void registerCardLabel(Long cardId, Long labelId) {
        Card card = findCard(cardId);
        Label label = findLabel(labelId);

        if (cardLabelRepository.existsByCard_IdAndLabel_Id(cardId, labelId)) {
            throw new CustomException(CustomErrorCode.CARD_LABEL_ALREADY_EXISTS);
        }

        cardLabelRepository.save(new CardLabel(card, label));
    }

    @Transactional
    public void cancelCardLabel(Long cardId, Long labelId) {
        findCard(cardId);
        findLabel(labelId);

        CardLabel cardLabel = cardLabelRepository.findByCard_IdAndLabel_Id(cardId, labelId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.CARD_LABEL_NOT_FOUND));

        cardLabelRepository.delete(cardLabel);
    }

    public List<CardResponseDto> findCardsByLabelId(Long labelId) {
        findLabel(labelId);

        return cardLabelRepository.findAllByLabel_Id(labelId).stream()
            .map(cardLabel -> CardResponseDto.of(cardLabel.getCard())).toList();
    }

    private Card findCard(Long cardId) {
        return cardRepository.findById(cardId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.CARD_NOT_FOUND));
    }

    private Label findLabel(Long labelId) {
        return labelRepository.findById(labelId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.LABEL_NOT_FOUND));
    }
}
