package com.example.twogether.checklist.dto;

import com.example.twogether.checklist.entity.CheckList;
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

    private Long clId;
    private String title;

    public static CheckListResponseDto of(CheckList checkList) {
        return CheckListResponseDto.builder()
            .clId(checkList.getId())
            .title(checkList.getTitle())
            //.check
            .build();
    }
}
