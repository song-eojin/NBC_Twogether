package com.example.twogether.card.controller;

import com.example.twogether.card.dto.CardResponseDto;
import com.example.twogether.card.service.CardLabelService;
import com.example.twogether.common.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "카드 라벨 CRD API", description = "카드에 라벨 등록/해제 기능 라벨 별 카드 조회 기능과 관련된 API 정보를 담고 있습니다.")
public class CardLabelController {

    private final CardLabelService cardLabelService;

    @PostMapping("/cards/{cardId}/labels/{labelId}")
    @Operation(summary = "카드에 라벨 등록")
    public ResponseEntity<ApiResponseDto> registerCardLabel(@PathVariable("cardId") Long cardId, @PathVariable("labelId") Long labelId) {
        cardLabelService.registerCardLabel(cardId, labelId);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드 라벨 등록"));
    }

    @DeleteMapping("/cards/{cardId}/labels/{labelId}")
    @Operation(summary = "카드에서 라벨 해제")
    public ResponseEntity<ApiResponseDto> cancelCardLabel(@PathVariable("cardId") Long cardId, @PathVariable("labelId") Long labelId) {
        cardLabelService.cancelCardLabel(cardId, labelId);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "카드 라벨 삭제"));
    }

    @GetMapping("/labels/{labelId}/filter")
    @Operation(summary = "라벨 별 카드 조회")
    public ResponseEntity<List<CardResponseDto>> findCardsByLabelId(@PathVariable Long labelId) {
        List<CardResponseDto> cardList = cardLabelService.findCardsByLabelId(labelId);
        return ResponseEntity.ok().body(cardList);
    }
}
