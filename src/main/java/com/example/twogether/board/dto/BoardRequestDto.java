package com.example.twogether.board.dto;

import com.example.twogether.board.entity.Board;
import com.example.twogether.user.entity.User;
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

    public Board toEntity(User user) {
        return Board.builder()
            .name(this.name)
            .color(this.color)
            .info(this.info)
            .user(user)
            .build();
    }
}