package com.example.twogether.card.dto;

import com.example.twogether.card.entity.CardCollaborator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardColEditResponseDto {

    private String deletedEmail;
    private String addedEmail;
}
