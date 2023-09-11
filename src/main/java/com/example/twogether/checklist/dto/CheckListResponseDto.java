package com.example.twogether.checklist.dto;

import com.example.twogether.checklist.entity.CheckList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CheckListResponseDto {

    private Long cardId;
    private Long clId;
    private String title;
    private List<ChlItemResponseDto> chlItems;

    public static CheckListResponseDto of(CheckList checkList) {
        return CheckListResponseDto.builder()
            .cardId(checkList.getCard().getId())
            .clId(checkList.getId())
            .title(checkList.getTitle())
            .chlItems(checkList.getCheckListItemList().stream().map(ChlItemResponseDto::of).toList())
            .build();
    }
}
