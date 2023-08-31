package com.example.twogether.comment.controller;

import com.example.twogether.comment.dto.CommentRequestDto;
import com.example.twogether.comment.dto.CommentResponseDto;
import com.example.twogether.comment.service.CommentService;
import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.common.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "댓글 CRUD API", description = "댓글 CRUD와 관련된 API 정보를 담고 있습니다.")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "카드에 댓글 등록")
    @PostMapping("/boards/{boardId}/cards/{cardId}/comments")
    public ResponseEntity<ApiResponseDto> createComment(
        @PathVariable Long boardId,
        @PathVariable Long cardId,
        @RequestBody CommentRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        commentService.createComment(boardId, cardId, requestDto, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "댓글 생성 완료"));
    }

    @Operation(summary = "단일 댓글 조회")
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> getComment(@PathVariable Long commentId) {
        CommentResponseDto resposneDto = commentService.getComment(commentId);
        return ResponseEntity.ok().body(resposneDto);
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseDto> editComment(
        @PathVariable Long commentId,
        @RequestBody CommentRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        commentService.editComment(commentId, requestDto, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "댓글 수정 완료"));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponseDto> deleteComment(
        @PathVariable Long commentId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(commentId, userDetails.getUser());

        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "댓글 삭제 완료"));
    }
}
