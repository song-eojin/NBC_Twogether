package com.example.twogether.deck.controller;

import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.deck.dto.DeckRequestDto;
import com.example.twogether.deck.service.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    @PostMapping("/decks")
    private ResponseEntity<ApiResponseDto> addDeck(@RequestBody DeckRequestDto requestDto) {
        deckService.addDeck(requestDto);
        return ResponseEntity.ok().body(new ApiResponseDto("덱 생성", HttpStatus.OK.value()));
    }

}
