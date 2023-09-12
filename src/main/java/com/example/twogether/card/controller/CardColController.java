package com.example.twogether.card.controller;

import com.example.twogether.card.dto.CardColEditResponseDto;
import com.example.twogether.card.dto.CardColRequestDto;
import com.example.twogether.card.dto.CardColsResponseDto;
import com.example.twogether.card.service.CardColService;
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

@Tag(name = "카드 협업자 API", description = "")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardColController {

    private final CardColService cardColService;

    @Operation(summary = "카드를 작업할 협업자 할당")
    @PostMapping("/cards/{cardId}/users")
    public ResponseEntity<ApiResponseDto> addCardCol(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long cardId,
        @RequestBody CardColRequestDto cardColRequestDto
    ) {
        cardColService.addCardCol(userDetails.getUser(), cardId, cardColRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드를 작업할 협업자를 할당하였습니다."));
    }

    @Operation(summary = "카드를 작업할 협업자 변경")
    @PutMapping("/cards/{cardId}/users")
    public ResponseEntity<ApiResponseDto> editCardCol(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long cardId,
        @RequestBody CardColEditResponseDto cardColEditResponseDto
    ) {
        cardColService.editCardCol(userDetails.getUser(), cardId, cardColEditResponseDto);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드에 할당된 협업자를 변경하였습니다."));
    }

    @Operation(summary = "카드를 작업할 협업자 삭제")
    @DeleteMapping("/cards/{cardId}/users")
    public ResponseEntity<ApiResponseDto> deleteCardCol(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long cardId,
        @RequestBody CardColRequestDto cardColRequestDto
    ) {
        cardColService.deleteCardCol(userDetails.getUser(), cardId, cardColRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드에 할당된 협업자를 삭제하였습니다."));
    }

    /* 필요하면 주석 풀 것 */
    //    @Operation(summary = "카드를 작업할 협업자 전체 조회")
//    @GetMapping("/cards/{cardId}/users")
//    public ResponseEntity<CardColsResponseDto> getCardCols(
//        @AuthenticationPrincipal UserDetailsImpl userDetails,
//        @PathVariable Long cardId
//    ) {
//        CardColsResponseDto addedCardCols = cardColService.getCardCols(userDetails.getUser(), cardId);
//        return ResponseEntity.ok().body(addedCardCols);
//    }
}