package com.example.twogether.common.error;

import lombok.Getter;

@Getter
public enum CustomErrorCode {
    // User
    USER_ALREADY_EXISTS("U001", "이미 존재하는 사용자입니다."),
    USER_NOT_FOUND("U002", "존재하지 않는 사용자입니다."),
    UNAUTHORIZED_REQUEST("U003", "승인되지 않은 요청입니다."),
    PASSWORD_MISMATCHED("U004", "기존 비밀번호와 일치하지 않습니다."),
    PASSWORD_RECENTLY_USED("U005", "최근 2회 이내에 사용한 적 있는 비밀번호입니다."),

    // Board
    BOARD_NOT_FOUND("B001", "존재하지 않는 보드입니다."),

    // Deck
    DECK_NOT_FOUND("D001", "존재하지 않는 덱입니다."),
    DECK_IS_NOT_ARCHIVE("D002", "덱이 보관된 상태에서만 삭제 가능합니다."),

    // Workspace
    WORKSPACE_NOT_FOUND("W001", "존재하지 않는 워크스페이스 입니다."),
    WORKSPACE_NOT_USER("W002", "본인이 작성한 워크스페이스만 수정/삭제 할 수 있습니다.");

    private final String errorCode;
    private final String errorMessage;

    CustomErrorCode(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
