package com.example.twogether.card.service;

import com.example.twogether.board.repository.BoardColRepository;
import com.example.twogether.board.repository.BoardRepository;
import com.example.twogether.card.dto.CardColEditResponseDto;
import com.example.twogether.card.dto.CardColRequestDto;
import com.example.twogether.card.dto.CardColsResponseDto;
import com.example.twogether.card.entity.Card;
import com.example.twogether.card.entity.CardCollaborator;
import com.example.twogether.card.repository.CardColRepository;
import com.example.twogether.card.repository.CardRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardColService {

    private final CardColRepository cardColRepository;
    private final CardRepository cardRepository;
    private final BoardRepository boardRepository;
    private final BoardColRepository boardColRepository;
    private final UserRepository userRepository;

    // 카드를 작업할 협업자 추가
    @Transactional
    public void addCardCol(User user, Long cardId, CardColRequestDto cardColRequestDto) {

        checkUserPermissions(user);
        Card addedCard = findCard(cardId);

        User foundUser = findUserByEmail(cardColRequestDto.getEmail());
        User addedUser = findAddedUser(foundUser);

        CardCollaborator foundCardCol = cardColRequestDto.toEntity(addedUser, addedCard);

        checkCardColPermissions(addedCard, foundCardCol.getEmail());
        cardColRepository.save(foundCardCol);
    }

    // 카드에 등록된 협업자 변경
    @Transactional
    public void editCardCol(User user, Long cardId, CardColEditResponseDto cardColEditResponseDto) {

        checkUserPermissions(user);

        Card editedCard = findCard(cardId);
        User foundUser = findUserByEmail(cardColEditResponseDto.getAddedEmail());
        User addedUser = findAddedUser(foundUser);

        CardCollaborator deletedCardCol = findCardCol(editedCard, cardColEditResponseDto.getDeletedEmail());
        CardCollaborator addedCardCol = CardColRequestDto.toEntity(addedUser, editedCard);

        checkCardColPermissions(editedCard, addedCardCol.getEmail());

        cardColRepository.delete(deletedCardCol);
        cardColRepository.save(addedCardCol);
    }

    // 카드에 등록된 협업자 삭제
    @Transactional
    public void deleteCardCol(User user, Long cardId, CardColRequestDto cardColRequestDto) {

        checkUserPermissions(user);

        Card deletedCard = findCard(cardId);

        CardCollaborator deletedCardCol = findCardCol(deletedCard, cardColRequestDto.getEmail());
        cardColRepository.delete(deletedCardCol);
    }
    
    // 카드에 등록된 협업자 전체 조회 - 프론트에서 필요할 경우를 대비해 CardColController에서 따로 구현
//    @Transactional(readOnly = true)
//    public CardColsResponseDto getCardCols(User user, Long cardId) {
//
//        checkUserPermissions(user);
//
//        Card foundCard = findCard(cardId);
//        List<CardCollaborator> foundCardCols = findCardCols(foundCard);
//
//        return CardColsResponseDto.of(foundCardCols);
//    }

    private List<CardCollaborator> findCardCols(Card foundCard) {

        return cardColRepository.findAlLByCard(foundCard);
    }

    private User findUserByEmail(String email) {

        return userRepository.findByEmail(email).orElseThrow(() ->
            new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    private User findAddedUser(User addedCardCol) {

        // 보드 생성자나 협업자로 등록되어 있지 않는 사람은 등록 불가 - addedCardCol(카드에 할당 당하는 협업자)의 email
        if (!boardRepository.existsByUser(addedCardCol) &&
            !boardColRepository.existsByEmail(addedCardCol.getEmail())) {

            throw new CustomException(CustomErrorCode.CARD_NOT_ACCESSIBLE);
        }

        return userRepository.findByEmail(addedCardCol.getEmail()).orElseThrow(() ->
            new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    private Card findCard(Long cardId) {

        return cardRepository.findById(cardId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.CARD_NOT_FOUND));
    }

    private CardCollaborator findCardCol(Card card, String email) {

        return cardColRepository.findByCardAndEmail(card, email).orElseThrow(() ->
            new CustomException(CustomErrorCode.CARD_COLLABORATOR_NOT_FOUND));
    }

    private void checkUserPermissions(User user) {

        // 보드 생성자나 협업자로 등록되어 있지 않는 사람은 카드 접근 불가 - user(로그인한 사람)의 email
        if (!boardRepository.existsByUser(user) &&
            !boardColRepository.existsByEmail(user.getEmail())) {

            throw new CustomException(CustomErrorCode.CARD_NOT_ACCESSIBLE);
        }
    }

    private void checkCardColPermissions(Card card, String email) {

        // 이미 카드에 할당된 협업자는 등록 불가
        if (cardColRepository.existsByCardAndEmail(card, email)) {
            throw new CustomException(CustomErrorCode.CARD_COLLABORATOR_ALREADY_EXISTS);
        }
    }
}
