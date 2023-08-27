package com.example.twogether.card.dto;

import com.example.twogether.card.entity.CardCollaborator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CardColsResponseDto {

    private List<CardColResponseDto> cardCollaborators;

    public static CardColsResponseDto of(List<CardCollaborator> cardCollaborators) {

        List<CardColResponseDto> cardColsResponseDto = cardCollaborators.stream().map(
            CardColResponseDto::of).toList();

        return CardColsResponseDto.builder()
            .cardCollaborators(cardColsResponseDto)
            .build();
    }}
