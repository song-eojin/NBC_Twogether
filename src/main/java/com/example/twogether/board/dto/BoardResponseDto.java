package com.example.twogether.board.dto;

import com.example.twogether.board.entity.Board;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponseDto {
    private Long id;
    private String name;
    private String author;
    private String color;
    private String info;
    private String message;
    private Integer statusCode;

//    private List<UserResponseDto> collaborators;
    private List<BoardUserResponseDto> boardUsers;
//    private List<AreaResponseDto> areas;

//    public static BoardResponseDto of(Board board) {
//        List<UserResponseDto> boardUsers = new ArrayList<>();
//        List<AreaResponseDto> areaResponseDtos = new ArrayList<>();
//        if (board.getBoardUsers().size() != 0) {
//            boardUsers = board.getBoardUsers().stream().map(BoardUser::getCollaborator)
//                .toList().stream().map(UserResponseDto::of).toList();
//        }
//        if (board.getAreas().size() !=0) {
//            areaResponseDtos = board.getAreas().stream().map(AreaResponseDto::of).toList();
//        }
//        return BoardResponseDto.builder()
//            .id(board.getId())
//            .name(board.getName())
//            .author(board.getAuthor().getUsername())
//            .color(board.getColor())
//            .info(board.getInfo())
//            .collaborators(boardUsers)
//            .areas(areaResponseDtos)
//            .boardUsers(board.getBoardUsers().stream().map(BoardUserResponseDto::of).toList())
//            //.areas(board.getAreas().stream().map(AreaResponseDto::of).toList())
//            .build();
//    }
}
