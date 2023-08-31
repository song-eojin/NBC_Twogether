package com.example.twogether.alarm.controller;

import com.example.twogether.alarm.dto.CardEditedsResponseDto;
import com.example.twogether.alarm.service.AlarmService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AlarmController {

    private final AlarmService alarmService;

    @Operation(summary = "알림 삭제")
    @DeleteMapping(value = "/alarms/{alarmId}")
    public ResponseEntity<ApiResponseDto> deleteAlarm(@PathVariable Long alarmId) {

        alarmService.deleteAlarm(alarmId);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "알림 삭제를 성공하셨습니다."));
    }

    @Operation(summary = "알림 읽기")
    @PutMapping(value = "/alarms/{alarmId}")
    public ResponseEntity<ApiResponseDto> readAlarm(@PathVariable Long alarmId) {

        alarmService.readAlarm(alarmId);
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "알림을 읽기를 성공하셨습니다."));
    }

    @Operation(summary = "알림 전체 조회", description = "로그인 시 본인이 가진 알림이 전체 조회되며, 이벤트 리스너가 새로운 알림 생성을 감지할 때마다 수행되는 기능입니다.")
    @GetMapping(value = "/alarms")
    public ResponseEntity<CardEditedsResponseDto> getAlarms(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        CardEditedsResponseDto alarms = alarmService.getAlarms(userDetails.getUser());
        return ResponseEntity.ok().body(alarms);
    }
}
