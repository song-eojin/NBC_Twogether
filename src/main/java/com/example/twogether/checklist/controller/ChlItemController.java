package com.example.twogether.checklist.controller;

import com.example.twogether.checklist.dto.ChlItemResponseDto;
import com.example.twogether.checklist.service.ChlItemService;
import com.example.twogether.common.dto.ApiResponseDto;
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
@Tag(name = "체크리스트 아이템 API", description = "체크리스트 아이템 API 정보")
public class ChlItemController {

    private final ChlItemService chlItemService;

    @Operation(summary = "체크리스트 아이템 생성")
    @PostMapping("/checklists/{chlId}/chlItems")
    public ResponseEntity<ApiResponseDto> createChlItem(
        @RequestBody String content,
        @PathVariable Long chlId
    ) {
        chlItemService.createChlItem(chlId, content);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.CREATED.value(), "체크리스트 아이템이 생성되었습니다."));
    }

    @Operation(summary = "체크리스트 아이템 내용 수정")
    @PatchMapping("/chlItems/{chlItemId}/content")
    public ResponseEntity<ApiResponseDto> editContent(
        @PathVariable Long chlItemId,
        @RequestBody String content
    ) {
        chlItemService.editContent(chlItemId, content);
        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "체크리스트 아이템 내용이 수정되었습니다."));
    }

    @Operation(summary = "체크리스트 아이템 체크 상태 변경")
    @PatchMapping("/chlItems/{chlItemId}/isChecked")
    public ResponseEntity<ApiResponseDto> updateIsChecked(
        @PathVariable Long chlItemId
    ) {
        chlItemService.updateIsChecked(chlItemId);
        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "체크리스트 아이템 체크 상태가 변경되었습니다."));
    }

    @Operation(summary = "체크리스트 아이템 삭제")
    @DeleteMapping("/chlItems/{chlItemId}")
    public ResponseEntity<ApiResponseDto> deleteChlItem(
        @PathVariable Long chlItemId
    ) {
        chlItemService.deleteChlItem(chlItemId);
        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "체크리스트 아이템이 삭제되었습니다."));
    }

    @Operation(summary = "체크리스트 아이템 단일 조회")
    @GetMapping("/chlItems/{chlItemId}")
    public ResponseEntity<ChlItemResponseDto> getChlItem(
        @PathVariable Long chlItemId
    ) {
        ChlItemResponseDto checkResponseDto = chlItemService.getChlItem(chlItemId);
        return ResponseEntity.ok().body(checkResponseDto);
    }

}
