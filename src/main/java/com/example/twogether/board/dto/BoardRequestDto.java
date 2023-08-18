package com.example.twogether.board.dto;

import com.example.twogether.board.entity.Board;
import com.example.twogether.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BoardRequestDto {
    @NotBlank
    private String title;
    private String color;
    private String info;

    public Board toEntity(User boardAuthor) {
        return Board.builder()
            .title(this.title)
            .color(this.color)
            .info("This is " + boardAuthor.getNickname() + "'s \"" + this.title + "\" board.")
            .boardAuthor(boardAuthor)
            .build();
    }
}