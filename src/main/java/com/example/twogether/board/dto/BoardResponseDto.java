package com.example.twogether.board.dto;

import com.example.twogether.board.entity.Board;
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
    private String nickname;
    private String title;
    private String color;
    private String info;

    /*협업자 관련*/
//    private List<UserResponseDto> collaborators;
//    private List<BoardUserResponseDto> boardUsers;

    public static BoardResponseDto of(Board board) {
//        List<UserResponseDto> boardUsers = new ArrayList<>();
//        if (board.getBoardUsers().size() != 0) {
//            boardUsers = board.getBoardUsers().stream().map(BoardUser::getCollaborator)
//                .toList().stream().map(UserResponseDto::of).toList();
//        }
        return BoardResponseDto.builder()
            .id(board.getId())
            .nickname(board.getBoardAuthor().getNickname())
            .title(board.getTitle())
            .color(board.getColor())
            .info(board.getInfo())
//            .collaborators(boardUsers)
//            .boardUsers(board.getBoardUsers().stream().map(BoardUserResponseDto::of).toList())
            .build();
    }
}
