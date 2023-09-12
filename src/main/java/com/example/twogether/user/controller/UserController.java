package com.example.twogether.user.controller;

import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.common.security.UserDetailsImpl;
import com.example.twogether.user.dto.EditPasswordRequestDto;
import com.example.twogether.user.dto.EditUserRequestDto;
import com.example.twogether.user.dto.LoginRequestDto;
import com.example.twogether.user.dto.SignupRequestDto;
import com.example.twogether.user.dto.UserResponseDto;
import com.example.twogether.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자의 회원 가입 기능과 관련된 API 정보를 담고 있습니다.")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto> signup(@Validated @RequestBody SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto(HttpStatus.CREATED.value(), "회원 가입 성공"));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        return null;
    }

    @Operation(summary = "사용자 로그아웃")
    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponseDto> logoutUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        HttpServletRequest request
    ) {
        userService.logoutUser(request, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "사용자 로그아웃"));
    }

    @Operation(summary = "사용자 본인 정보 조회")
    @GetMapping("/info")
    public ResponseEntity<UserResponseDto> getUserInfo(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok().body(UserResponseDto.of(userDetails.getUser()));
    }

    @Operation(summary = "사용자 정보 수정")
    @PatchMapping("/info")
    public ResponseEntity<ApiResponseDto> editUserInfo(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody EditUserRequestDto requestDto
    ) {
        userService.editUserInfo(requestDto, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "회원 정보 수정 성공"));
    }

    @Operation(summary = "사용자 비밀번호 수정")
    @PatchMapping("/password")
    public ResponseEntity<ApiResponseDto> editUserPassword(
        @Validated @RequestBody EditPasswordRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        userService.editUserPassword(requestDto, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "회원 비밀번호 수정 성공"));
    }

    @Operation(summary = "사용자 프로필 사진 수정")
    @PutMapping("/icon")
    public ResponseEntity<ApiResponseDto> editIcon(
        @RequestPart MultipartFile multipartFile,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException{
        userService.editIcon(multipartFile, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "프로필 사진 수정 성공"));
    }

    @Operation(summary = "사용자 프로필 사진 기본으로 수정")
    @PutMapping("/default")
    public ResponseEntity<ApiResponseDto> defaultIcon(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        userService.defaultIcon(userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "기본 프로필 사진으로 수정 성공"));
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/{id}/signout")
    public ResponseEntity<ApiResponseDto> deleteUserInfo(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        userService.deleteUserInfo(id, userDetails.getUser());
        return ResponseEntity.ok().body(new ApiResponseDto(HttpStatus.OK.value(), "회원 탈퇴 성공"));
    }
}