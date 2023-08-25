package com.example.twogether.checklist.service;

import com.example.twogether.card.entity.Card;
import com.example.twogether.card.repository.CardRepository;
import com.example.twogether.checklist.dto.CheckListRequestDto;
import com.example.twogether.checklist.dto.CheckListResponseDto;
import com.example.twogether.checklist.entity.CheckList;
import com.example.twogether.checklist.repository.CheckListRepository;
import com.example.twogether.checklist.repository.ChlItemRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckListService {

    private final CheckListRepository chlRepository;
    private final CardRepository cardRepository;
    private final ChlItemRepository chlItemRepository;

    @Transactional
    public CheckListResponseDto createCheckList(Long cardId, CheckListRequestDto chlRequestDto) {
        Card card = findCardById(cardId);
        CheckList checkList = chlRequestDto.toEntity(card);
        chlRepository.save(checkList);
        return CheckListResponseDto.of(checkList);
    }

    @Transactional
    public CheckListResponseDto editCheckList(Long chlId, CheckListRequestDto chlRequestDto) {
        CheckList checkList = findCheckListById(chlId);
        checkList.update(chlRequestDto);
        return CheckListResponseDto.of(checkList);
    }

    @Transactional
    public void deleteCheckList(Long chlId) {
        CheckList checkList = findCheckListById(chlId);
        chlItemRepository.deleteAllByCheckList_Id(chlId);
        chlRepository.delete(checkList);
    }

    @Transactional(readOnly = true)
    public CheckListResponseDto getCheckList(Long chlId) {
        CheckList checkList = findCheckListById(chlId);
        return CheckListResponseDto.of(checkList);
    }

    @Transactional(readOnly = true)
    public List<CheckListResponseDto> getCheckLists(Long cardId) {
        findCardById(cardId);
            List<CheckList> checkLists = chlRepository.findAllByCardId(cardId);
            return checkLists.stream().map(CheckListResponseDto::of).toList();
    }

    private CheckList findCheckListById(Long chlId) {
        return chlRepository.findById(chlId).orElseThrow(() ->
            new CustomException(CustomErrorCode.CHECKLIST_NOT_FOUND));    }

    private Card findCardById(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(() ->
            new CustomException(CustomErrorCode.CARD_NOT_FOUND));
    }
}
