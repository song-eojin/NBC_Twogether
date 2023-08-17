package com.example.twogether.board.dto;

import com.example.twogether.board.entity.Board;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardRequestDto {
    @NotBlank
    private String name;
    private String color;
    private String info;

    public Board toEntity(User author) {
        return Board.builder()
            .name(this.name)
            .color(this.color)
            .info(this.info)
            .author(author)
            .build();
    }
}