package com.example.twogether.card.dto;

import com.example.twogether.board.dto.BoardColResponseDto;
import com.example.twogether.board.entity.BoardCollaborator;
import com.example.twogether.card.entity.CardCollaborator;
import com.example.twogether.workspace.dto.WpColResponseDto;
import com.example.twogether.workspace.entity.WorkspaceCollaborator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CardColResponseDto {

    private Long cardColId;
    private String email;
    private String nickname;

    public static CardColResponseDto of(CardCollaborator cardCollaborator) {
        return CardColResponseDto.builder()
            .cardColId(cardCollaborator.getId())
            .email(cardCollaborator.getUser().getEmail())
            .nickname(cardCollaborator.getUser().getNickname())
            .build();
    }
}
