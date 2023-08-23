package com.example.twogether.checklist.controller;

import com.example.twogether.checklist.dto.CheckListRequestDto;
import com.example.twogether.checklist.dto.CheckListResponseDto;
import com.example.twogether.checklist.service.CheckListService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "체크리스트 API", description = "체크리스트 API 정보")
public class CheckListController {

    private final CheckListService chlService;

    @Operation(summary = "체크리스트 생성")
    @PostMapping("/cards/{cardId}/checklists")
    public ResponseEntity<ApiResponseDto> createCheckList(
        @RequestBody CheckListRequestDto chlRequestDto,
        @PathVariable Long cardId
    ) {
        chlService.createCheckList(cardId, chlRequestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.CREATED.value(), "체크리스트가 생성되었습니다."));
    }

    @Operation(summary = "체크리스트 수정")
    @PutMapping("/checklists/{chlId}")
    public ResponseEntity<ApiResponseDto> editCheckList(
        @PathVariable Long chlId,
        @RequestBody CheckListRequestDto chlRequestDto
    ) {
        chlService.editCheckList(chlId, chlRequestDto);
        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "체크리스트가 수정되었습니다."));
    }

    @Operation(summary = "체크리스트 삭제")
    @DeleteMapping("/checklists/{chlId}")
    public ResponseEntity<ApiResponseDto> deleteCheckList(
        @PathVariable Long chlId
    ) {
        chlService.deleteCheckList(chlId);
        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "체크리스트가 삭제되었습니다."));
    }

    @Operation(summary = "체크리스트 단일 조회")
    @GetMapping("/checklists/{chlId}")
    public ResponseEntity<CheckListResponseDto> getCheckList(
        @PathVariable Long chlId
    ) {
        CheckListResponseDto chlResponseDto = chlService.getCheckList(chlId);
        return ResponseEntity.ok().body(chlResponseDto);
    }
    @Operation(summary = "해당 카드의 체크리스트 전체 조회")
    @GetMapping("/card/{cardId}/checklists")
    public ResponseEntity<List<CheckListResponseDto>> getCheckLists(
        @PathVariable Long cardId
    ) {
        List<CheckListResponseDto> chlResponseDtos = chlService.getCheckLists(cardId);
        return ResponseEntity.ok().body(chlResponseDtos);
    }
}
