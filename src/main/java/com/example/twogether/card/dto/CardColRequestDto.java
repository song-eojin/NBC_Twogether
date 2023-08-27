package com.example.twogether.card.dto;

import com.example.twogether.card.entity.Card;
import com.example.twogether.card.entity.CardCollaborator;
import com.example.twogether.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardColRequestDto {

    private String email;
    public static CardCollaborator toEntity(User user, Card card) {
        return CardCollaborator.builder()
            .email(user.getEmail())
            .user(user)
            .card(card)
            .build();
    }
}
