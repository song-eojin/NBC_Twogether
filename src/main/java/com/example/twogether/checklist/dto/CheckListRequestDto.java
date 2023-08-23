package com.example.twogether.checklist.dto;

import com.example.twogether.card.entity.Card;
import com.example.twogether.checklist.entity.CheckList;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CheckListRequestDto {

    private String title;

    public CheckList toEntity(Card card) {
        return CheckList.builder()
            .title(title)
            .card(card)
            .build();
    }
}
