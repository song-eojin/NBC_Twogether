package com.example.twogether.card.controller;

import com.example.twogether.card.dto.CardEditRequestDto;
import com.example.twogether.card.dto.CardResponseDto;
import com.example.twogether.card.dto.DateRequestDto;
import com.example.twogether.card.dto.MoveCardRequestDto;
import com.example.twogether.card.entity.Card;
import com.example.twogether.card.service.CardService;
import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.common.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "카드 API", description = "카드 CRUD, 이동 기능, 카드 라벨 수정, 작업자 할당, 마감일 설정과 관련된 API 정보를 담고 있습니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "카드 생성", description = "카드를 생성할 때 자동으로 가장 끝에 있는 카드의 "
        + "position + cycle(128)으로 position을 설정한다.")
    @PostMapping("/decks/{deckId}/cards")
    private ResponseEntity<ApiResponseDto> addCard(
        @PathVariable Long deckId,
        @RequestBody String title
    ) {

        cardService.addCard(deckId, title);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드 생성"));
    }

    @Operation(summary = "카드 수정", description = "requestDto에 title 혹은 description이 null이라면 수정하지 않고 내버려두도록 설정")
    @PatchMapping("/cards/{cardId}")
    private ResponseEntity<ApiResponseDto> editCard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long cardId,
        @RequestBody CardEditRequestDto requestDto
    ) {

        cardService.editCard(userDetails.getUser(), cardId, requestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드 수정"));
    }

    @Operation(summary = "협업 마감일 수정")
    @PutMapping("/cards/{cardId}/date")
    private ResponseEntity<ApiResponseDto> editDate(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long cardId,
        @RequestBody DateRequestDto requestDto
    ) {

        cardService.editDate(userDetails.getUser(), cardId, requestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "마감일 수정"));
    }

    @Operation(summary = "카드 삭제", description = "카드가 보관 상태라면 삭제되지 않도록 설정")
    @DeleteMapping("/cards/{cardId}")
    private ResponseEntity<ApiResponseDto> deleteCard(@PathVariable Long cardId) {

        cardService.deleteCard(cardId);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드 삭제"));
    }

    @Operation(summary = "카드 보관/복구", description = "카드를 삭제하기 전 보관상태로 만들고, 복구하는 기능")
    @PutMapping("/cards/{cardId}/archive")
    private ResponseEntity<ApiResponseDto> archiveCard(@PathVariable Long cardId) {

        cardService.archiveCard(cardId);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드 보관/복구"));
    }

    @Operation(summary = "카드 이동", description = "카드를 이동하면 position 값을 이동하고자 하는 카드와 카드 사이의 "
        + "position 중간 값으로 설정, board 도 바꿀 수 있음.")
    @PutMapping("/cards/{cardId}/move")
    private ResponseEntity<ApiResponseDto> moveCard(
        @PathVariable Long cardId,
        @RequestBody MoveCardRequestDto requestDto
    ) {

        cardService.moveCard(cardId, requestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드 이동"));
    }

    @Operation(summary = "파일 첨부", description = "S3를 사용해 이미지 및 멀티파트 파일을 업로드한다.")
    @PutMapping("/cards/{cardId}/file")
    private ResponseEntity<ApiResponseDto> uploadFile(
        @PathVariable Long cardId,
        @RequestPart MultipartFile multipartFile
    ) throws IOException {

        cardService.uploadFile(cardId, multipartFile);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "파일 첨부"));
    }

    @Operation(summary = "카드 단일 조회")
    @GetMapping("/cards/{cardId}")
    private ResponseEntity<CardResponseDto> getCard(@PathVariable Long cardId) {

        CardResponseDto responseDto = cardService.getCard(cardId);
        return ResponseEntity.ok().body(responseDto);
    }
}
