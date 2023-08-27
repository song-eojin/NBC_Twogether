package com.example.twogether.card.dto;

import com.example.twogether.card.entity.Card;
import com.example.twogether.card.entity.CardCollaborator;
import com.example.twogether.comment.dto.CommentResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseDto {
    private Long id;
    private String title;
    private String description;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;
    private String attachment;
    private float position;
    private List<CommentResponseDto> comments;
    private List<CardColResponseDto> cardCollaborators;

    public static CardResponseDto of(Card card) {
        return CardResponseDto.builder()
            .id(card.getId())
            .title(card.getTitle())
            .description(card.getDescription())
            .dueDate(card.getDueDate())
            .attachment(card.getAttachment())
            .position(card.getPosition())
            .comments(card.getComments().stream().map(CommentResponseDto::of).toList())
            .cardCollaborators(card.getCardCollaborators().stream().map(
                CardColResponseDto::of).toList())
            .build();
    }

}
