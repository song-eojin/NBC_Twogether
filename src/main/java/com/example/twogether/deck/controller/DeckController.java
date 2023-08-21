package com.example.twogether.deck.controller;

import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.deck.dto.DeckResponseDto;
import com.example.twogether.deck.dto.MoveDeckRequestDto;
import com.example.twogether.deck.service.DeckService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "덱 API", description = "덱 CRUD, 이동 기능과 관련된 API 정보를 담고 있습니다.")
public class DeckController {

    private final DeckService deckService;

    @Operation(summary = "덱 생성", description = "덱을 생성할 때 자동으로 가장 끝에 있는 덱의 "
        + "position + cycle(128)으로 position을 설정한다.")
    @PostMapping("/boards/{boardId}/decks")
    private ResponseEntity<ApiResponseDto> addDeck(@PathVariable Long boardId, @RequestBody String title) {
        deckService.addDeck(boardId, title);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "덱 생성"));
    }

    @Operation(summary = "덱 단일 조회")
    @GetMapping("/decks/{id}")
    private ResponseEntity<DeckResponseDto> getDeck(@PathVariable Long id) {
        DeckResponseDto responseDto = deckService.getDeck(id);
        return ResponseEntity.ok().body(responseDto);
    }

    @Operation(summary = "덱 title 수정")
    @PutMapping("/decks/{id}")
    private ResponseEntity<ApiResponseDto> editDeck(@PathVariable Long id, @RequestBody String title) {
        deckService.editDeck(id, title);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "덱 수정"));
    }

    @Operation(summary = "덱 보관/복구", description = "id와 일치하는 덱이 deleted 상태면 복구하고, 아니면 deleted 상태로 만듭니다.")
    @PatchMapping("/decks/{id}/archive")
    private ResponseEntity<ApiResponseDto> archiveDeck(@PathVariable Long id) {
        deckService.archiveDeck(id);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "덱 보관/복구"));
    }

    @Operation(summary = "덱 삭제")
    @DeleteMapping("/decks/{id}")
    private ResponseEntity<ApiResponseDto> deleteDeck(@PathVariable Long id) {
        deckService.deleteDeck(id);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "덱 삭제"));
    }

    // 덱 이동
    @Operation(summary = "덱 이동", description = "덱을 이동하면 position 값을 이동하고자 하는 덱과 덱 사이의 "
        + "position 중간 값으로 설정")
    @PatchMapping("/decks/{id}/move")
    private ResponseEntity<ApiResponseDto> moveDeck(@PathVariable Long id, @RequestBody
        MoveDeckRequestDto requestDto) {
        deckService.moveDeck(id, requestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "덱 이동"));
    }
}
