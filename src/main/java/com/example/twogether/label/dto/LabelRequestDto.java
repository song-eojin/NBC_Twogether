package com.example.twogether.label.dto;

import com.example.twogether.board.entity.Board;
import com.example.twogether.label.entity.Label;
import lombok.Getter;

@Getter
public class LabelRequestDto {
    private String title;
    private String color;

    public Label toEntity(Board board){
        return Label.builder()
                .title(title)
                .color(color)
                .board(board)
                .build();
    }
}
