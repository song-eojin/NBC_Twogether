package com.example.twogether.common.error;

import lombok.Getter;

@Getter
public enum CustomErrorCode {
    // User
    EMAIL_NOT_VERIFIED("U001", "이메일 인증이 완료되지 않았습니다."),
    USER_NOT_FOUND("U002", "존재하지 않는 사용자입니다."),
    UNAUTHORIZED_REQUEST("U003", "승인되지 않은 요청입니다."),
    PASSWORD_MISMATCHED("U004", "기존 비밀번호와 일치하지 않습니다."),
    PASSWORD_RECENTLY_USED("U005", "최근 2회 이내에 사용한 적 있는 비밀번호입니다."),
    ICON_UPLOAD_FAIL("U006", "프로필 사진 기본으로 변경을 실패했습니다."),

    // Email
    EMAIL_ALREADY_USED("EM001", "이미 사용 중인 이메일입니다."),
    EMAIL_SEND_FAILED("EM002", "이메일 전송에 실패했습니다"),
    EMAIL_NOT_FOUND("EM003", "인증을 요청받은 메일이 아닙니다."),
    INVALID_CERTIFICATION_NUMBER("EM004", "이메일 인증 번호가 일치하지 않습니다."),

    // Token
    ACCESS_TOKEN_INVALID("T001", "유효하지 않은 Access 토큰입니다"),
    ALL_TOKENS_EXPIRED("T002", "RefreshToken이 만료되었습니다."),
    REFRESH_TOKEN_NOT_EXISTS("T003", "Refresh 토큰이 존재하지 않습니다."),

    // Workspace
    WORKSPACE_COLLABORATOR_ALREADY_EXISTS("W001", "이미 존재하는 워크스페이스 협업자입니다."),
    WORKSPACE_NOT_FOUND("W002", "존재하지 않는 워크스페이스입니다."),
    NOT_YOUR_WORKSPACE("W003", "해당 기능은 워크스페이스를 생성한 사람만 접근 할 수 있습니다."),
    NOT_PARTICIPATED_WORKSPACE("W004", "참여하지 않은 워크스페이스입니다."),
    NO_BOARDS_IN_THIS_WORKSPACE("W005", "워크스페이스에 존재하는 보드가 없습니다."),
    THIS_IS_YOUR_WORKSPACE("W006", "당신은 워크스페이스의 관리자입니다."),
    UNINVITED_WORKSPACE("W007", "초대되지 않은 워크스페이스를 조회하였습니다."),

    // Board
    BOARD_NOT_FOUND("B001", "존재하지 않는 보드입니다."),
    NOT_YOUR_BOARD("B002", "해당 기능은 보드를 생성한 사람만 접근할 수 있습니다."),
    NOT_PARTICIPATED_BOARD("B003", "참여하지 않은 보드입니다."),
    THIS_IS_YOUR_BOARD("B004", "당신은 보드의 관리자입니다."),
    UNINVITED_BOARD("B005", "초대되지 않은 보드를 조회하였습니다."),
    BOARD_ALREADY_ARCHIVED("B006", "이미 삭제 보관된 보드입니다."),
    BOARD_COLLABORATOR_NOT_ACCESSIBLE("B007", "보드의 협업자 기능 사용에 실패했습니다."),
    BOARD_COLLABORATOR_ALREADY_EXISTS("B008", "이미 존재하는 보드 협업자입니다."),
    BOARD_COLLABORATOR_ALREADY_OUT("B009", "이미 추방된 보드 협업자입니다."),
    BOARD_COLLABORATOR_NOT_FOUND("B010", "존재하지 않는 보드 협업자입니다."),

    // Deck
    DECK_NOT_FOUND("D001", "존재하지 않는 덱입니다."),
    DECK_IS_NOT_ARCHIVE("D002", "덱이 보관된 상태에서만 삭제 가능합니다."),

    // Card
    CARD_NOT_FOUND("C001", "존재하지 않는 카드입니다."),
    CARD_IS_NOT_ARCHIVE("C002", "카드가 보관된 상태에서만 삭제 가능합니다."),
    CARD_COLLABORATOR_NOT_FOUND("C003", "할당되지 않은 카드 협업자입니다."),
    CARD_COLLABORATOR_ALREADY_EXISTS("C004", "이미 할당된 카드 협업자입니다."),
    CARD_NOT_ACCESSIBLE("C005", "보드에 등록되지 않는 협업자는 카드 접근이 제한됩니다."),

    // S3
    S3_FILE_CONVERT_FAIL("S001", "멀티파트 파일 변환 실패"),
    S3_FILE_UPLOAD_FAIL("S002", "파일 업로드 실패"),

    // Label
    LABEL_ALREADY_EXISTS("L001", "이미 존재하는 라벨입니다."),
    LABEL_NOT_FOUND("L002", "존재하지 않는 라벨입니다."),

    // CardLabel
    CARD_LABEL_ALREADY_EXISTS("CL001", "이미 해당 카드에 등록된 라벨입니다."),
    CARD_LABEL_NOT_FOUND("CL002", "해당 카드에 등록되지 않은 라벨입니다."),

    // CheckList
    CHECKLIST_NOT_FOUND("CHL001", "등록되지 않은 체크리스트입니다."),
    CHECKLIST_ITEM_NOT_FOUND("CHL002", "등록되지 않은 체크리스트 아이템입니다."),

    // Comment
    COMMENT_NOT_FOUND("C001", "존재하지 않는 댓글입니다."),
    NOT_YOUR_COMMENT("C002", "본인이 작성한 댓글이 아닙니다."),

    // Alarm
    ALARM_NOT_FOUND("A001", "존재하지 않는 알림입니다."),
    FAIL_SEND_ALARM_TO_CLIENT("A002", "클라이언트로의 데이터 전송에 실패했습니다.");

    private final String code;
    private final String errorMessage;

    CustomErrorCode(String code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
