package com.example.twogether.Card.controller;

import com.example.twogether.Card.dto.CardEditRequestDto;
import com.example.twogether.Card.service.CardService;
import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.Card.dto.CardResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "카드 API", description = "카드 CRUD, 이동 기능, 카드 라벨 수정, 작업자 할당, 마감일 설정과 관련된 API 정보를 담고 있습니다.")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "카드 생성", description = "카드를 생성할 때 자동으로 가장 끝에 있는 카드의 "
        + "position + cycle(128)으로 position을 설정한다.")
    @PostMapping("/decks/{deckId}/cards")
    private ResponseEntity<ApiResponseDto> addCard(@PathVariable Long deckId, @RequestBody String title) {
        cardService.addCard(deckId, title);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드 생성"));
    }

    @Operation(summary = "덱 단일 조회")
    @GetMapping("/cards/{id}")
    private ResponseEntity<CardResponseDto> getCard(@PathVariable Long id) {
        CardResponseDto responseDto = cardService.getCard(id);
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "덱 수정", description = "requestDto에 title 혹은 description이 null이라면 수정하지 않고 내버려두도록 설정")
    @PatchMapping("/cards/{id}")
    private ResponseEntity<ApiResponseDto> editCard(@PathVariable Long id, @RequestBody CardEditRequestDto requestDto) {
        cardService.editCard(id, requestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드 수정"));
    }

    @Operation(summary = "덱 삭제", description = "덱이 보관 상태라면 삭제되지 않도록 설정")
    @DeleteMapping("/cards/{id}")
    private ResponseEntity<ApiResponseDto> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드 삭제"));
    }
}
