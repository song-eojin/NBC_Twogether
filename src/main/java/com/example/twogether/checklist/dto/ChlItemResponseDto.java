package com.example.twogether.checklist.dto;

import com.example.twogether.checklist.entity.CheckListItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChlItemResponseDto {
    private Long cardId;
    private Long chlItemId;
    private String content;
    private boolean checked;

    public static ChlItemResponseDto of(CheckListItem chlItem) {
        return ChlItemResponseDto.builder()
            .cardId(chlItem.getCheckList().getCard().getId())
            .chlItemId(chlItem.getId())
            .content(chlItem.getContent())
            .checked(chlItem.isChecked())
            .build();
    }
}
