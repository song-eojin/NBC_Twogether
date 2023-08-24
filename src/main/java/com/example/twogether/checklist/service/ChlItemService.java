package com.example.twogether.checklist.service;

import com.example.twogether.checklist.dto.ChlItemResponseDto;
import com.example.twogether.checklist.entity.CheckList;
import com.example.twogether.checklist.entity.CheckListItem;
import com.example.twogether.checklist.repository.CheckListRepository;
import com.example.twogether.checklist.repository.ChlItemRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChlItemService {
    private final CheckListRepository chlRepository;
    private final ChlItemRepository chlItemRepository;

    @Transactional
    public ChlItemResponseDto createChlItem(Long chlId, String content) {
        CheckList checkList = findCheckListById(chlId);
        CheckListItem chlItem = CheckListItem.builder()
                .content(content)
                .checkList(checkList)
                .build();
        chlItemRepository.save(chlItem);
        return ChlItemResponseDto.of(chlItem);
    }

    @Transactional
    public ChlItemResponseDto editContent(Long chlItemId, String content) {
        CheckListItem chlItem = findChlItemById(chlItemId);
        chlItem.updateContent(content);
        return ChlItemResponseDto.of(chlItem);
    }

    @Transactional
    public ChlItemResponseDto updateIsChecked(Long chlItemId) {
        CheckListItem chlItem = findChlItemById(chlItemId);
        chlItem.updateIsChecked();
        return ChlItemResponseDto.of(chlItem);
    }

    @Transactional
    public void deleteChlItem(Long chlItemId) {
        CheckListItem chlItem = findChlItemById(chlItemId);
        chlItemRepository.delete(chlItem);
    }

    public ChlItemResponseDto getChlItem(Long chlItemId) {
        CheckListItem chlItem = findChlItemById(chlItemId);
        return ChlItemResponseDto.of(chlItem);
    }

    private CheckList findCheckListById(Long chlId) {
        return chlRepository.findById(chlId).orElseThrow(() ->
            new CustomException(CustomErrorCode.CHECKLIST_NOT_FOUND));
    }

    private CheckListItem findChlItemById(Long checkId) {
        return chlItemRepository.findById(checkId).orElseThrow(() ->
            new CustomException(CustomErrorCode.CHECKLIST_ITEM_NOT_FOUND));
    }


}
