package com.example.twogether.deck.dto;

import com.example.twogether.deck.entity.Deck;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeckRequestDto {

    private String title;
    private Long parentId;
    private boolean deleted;

    public Deck toEntity() {
        return Deck.builder()
            .title(this.title)
            .parentId(this.parentId)
            .deleted(this.deleted)
            .build();
    }

}
