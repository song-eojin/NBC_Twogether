package com.example.twogether.board.controller;

public class BoardMemberController {
/*    // 보드 작업자 초대 - 허락받아야 초대되는 로직으로 develop 할지 고민 중
    @Operation(summary = "칸반 보드에 작업자 등록")
    @PostMapping("/boards/{id}/invite")
    public ResponseEntity<ApiResponseDto> addCollaborator(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id,
        @RequestBody BoardCollaboRequestDto requestDto) {

        boardService.addCollaborator(id, requestDto.getEmail());

        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "칸반 보드에 협업자가 등록되었습니다."));
    }

    // 보드 협업자 명단 수정
    @Operation(summary = "칸반 보드의 협업자 명단 수정")
    @PutMapping("/boards/{id}/invite")
    public ResponseEntity<ApiResponseDto> updateCollaborator(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        Board board = boardService.findBoard(userDetails.getUser(), boardId);
        BoardMember boardUser = boardService.findCollaborator(boardUserId);
        User newCollaborator = userService.findUserByUserid(userDetails.getUser().getId());

        boardService.updateCollaborator(board, boardUser, newCollaborator);
        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "칸반 보드의 협업자가 수정되었습니다."));
    }

    // 보드 협업자 전체 조회
    @GetMapping("/boards/{id}/invite")
    public ResponseEntity<BoardUsersResponseDto> getBoardUsers(@PathVariable Long boardId) {
        BoardUsersResponseDto boardUser = boardService.getBoardUsers(boardId);

        return ResponseEntity.ok().body(boardUser);
    }

    // 보드 협업자 삭제
    @Operation(summary = "update Collaborators of Board", description = "칸반 보드의 협업자 명단 수정")
    @DeleteMapping("/boards/{id}/invite")
    public ResponseEntity<ApiResponseDto> deleteCollaborator(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId,
        @PathVariable Long userId
    ) {
        boardService.deleteCollaborator(boardId, userId);
        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "칸반 보드의 협업자가 삭제되었습니다."));
    }

    // 보드 전체 조회 (협업 초대 받은 보드)
    @Operation(summary = "get collaborator's boards", description = "협업하고 있는 칸반 보드 전체 조회")
    @GetMapping("/boards/collaborators")
    public ResponseEntity<BoardsResponseDto> getCollaboratedBoards(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardsResponseDto result = boardService.getCollaboratedBoards(userDetails);
        return ResponseEntity.ok().body(result);
    }

    // 보드 단건 조회 (협업 초대 받은 보드)
    @Operation(summary = "get collaborator's board by id", description = "협업하고 있는 칸반 보드 단건 조회")
    @GetMapping("/boards/collaborators/{id}")
    public ResponseEntity<BoardResponseDto> getCollaboratedBoardById(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        BoardResponseDto result = boardService.getCollaboratedBoardById(userDetails.getUser(), id);
        return ResponseEntity.ok().body(result);
    }*/
}
