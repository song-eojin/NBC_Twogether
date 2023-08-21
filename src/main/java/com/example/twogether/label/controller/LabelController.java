package com.example.twogether.label.controller;

import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.label.dto.LabelRequestDto;
import com.example.twogether.label.dto.LabelResponseDto;
import com.example.twogether.label.service.LabelService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "카드 CRUD API", description = "카드 생성, 조회, 수정, 삭제 기능과 관련된 API 정보를 담고 있습니다.")
public class LabelController {

    private final LabelService labelService;

    @PostMapping("/labels")
    @Operation(summary = "새로운 라벨 생성")
    public ResponseEntity<ApiResponseDto> createLabel(@RequestBody LabelRequestDto requestDto, @RequestParam("boardId") Long boardId) {
        labelService.createLabel(requestDto, boardId);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "라벨 생성 완료"));
    }

    @GetMapping("/labels")
    @Operation(summary = "라벨 리스트 조회")
    public ResponseEntity<List<LabelResponseDto>> getLabels(@RequestParam Long boardId) {
        List<LabelResponseDto> labelResponseDtos = labelService.getLabels(boardId);
        return ResponseEntity.ok().body(labelResponseDtos);
    }

    @PutMapping("/labels/{labelId}")
    @Operation(summary = "라벨 정보 수정")
    public ResponseEntity<ApiResponseDto> editLabel(@PathVariable Long labelId, @RequestBody LabelRequestDto requestDto) {
        labelService.editLabel(labelId, requestDto);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "라벨 수정 완료."));
    }

    @DeleteMapping("/labels/{labelId}")
    @Operation(summary = "라벨 삭제")
    public ResponseEntity<ApiResponseDto> deleteLabel(@PathVariable Long labelId) {
        labelService.deleteLabel(labelId);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "라벨 삭제 완료."));
    }
}
