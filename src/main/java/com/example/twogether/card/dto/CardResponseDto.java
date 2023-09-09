package com.example.twogether.card.dto;

import com.example.twogether.card.entity.Card;
import com.example.twogether.checklist.dto.CheckListResponseDto;
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
    private String content;
    private boolean archived;
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;
    private String attachment;
    private float position;
    private List<CheckListResponseDto> checkLists;
    private List<CommentResponseDto> comments;
//    private List<CardLabelResponseDto> cardLabels;
    private List<CardColResponseDto> cardCollaborators;

    public static CardResponseDto of(Card card) {
        return CardResponseDto.builder()
            .id(card.getId())
            .title(card.getTitle())
            .content(card.getContent())
            .archived(card.isArchived())
            .dueDate(card.getDueDate())
            .attachment(card.getAttachment())
            .position(card.getPosition())
//            .checkLists(card.getCheckLists().stream().map(CheckListResponseDto:of).toList())
            .comments(card.getComments().stream().map(CommentResponseDto::of).toList())
            .cardCollaborators(card.getCardCollaborators().stream().map(
                CardColResponseDto::of).toList())
            .build();
    }

}
