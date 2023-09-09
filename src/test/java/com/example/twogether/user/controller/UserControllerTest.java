package com.example.twogether.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.twogether.user.dto.EditPasswordRequestDto;
import com.example.twogether.user.dto.EditUserRequestDto;
import com.example.twogether.user.dto.LoginRequestDto;
import com.example.twogether.user.dto.SignupRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    private static final String BASE_URL = "/api/users";

    private String token;

    @BeforeEach
    void init() throws Exception {
        login();
    }

    @Test
    @DisplayName("로그인")
    void login() throws Exception {
        // given
        String email = "user1@mail.com";
        String password = "user123!@#";

        // when
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail(email);
        requestDto.setPassword(password);

        String body = mapper.writeValueAsString(requestDto);

        // then
        MvcResult result = mvc.perform(post(BASE_URL + "/login")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        token = Objects.requireNonNull(result.getResponse().getHeader("Authorization"));
        Assertions.assertEquals("Bearer", Objects.requireNonNull(token.substring(0, 6)));
    }

    @Test
    @DisplayName("사용자 정보 수정")
    void editUserInfo() throws Exception {
        // given
        String nickname = "I'm Owl";
        String introduction = "This is my child owo";

        // when
        String body = mapper.writeValueAsString(
            EditUserRequestDto.builder()
                .nickname(nickname).introduction(introduction)
                .build()
        );

        // then
        mvc.perform(patch(BASE_URL + "/info")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
            )
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("사용자 정보 삭제")
    void deleteUserInfo() throws Exception {
        // given
        String email = "user1@mail.com";

        // when-then. 사용자 본인 정보 가져오기
        MvcResult result = mvc.perform(get(BASE_URL + "/info")
                .header("Authorization", token)
            )
            .andExpect(status().isOk())
            .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject) parser.parse(jsonResult);

        Long userId = (Long) jsonObj.get("id");
        Assertions.assertEquals(email, jsonObj.get("email"));

        // then
        mvc.perform(delete(BASE_URL + "/" + userId + "/signout")
                .header("Authorization", token)
            )
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("사용자 비밀번호 수정")
    void editUserPassword() throws Exception {
        // given
        String password = "user123!@#";
        String newPassword = "user234@#$";

        // when-then. 비밀번호 정상 변경
        String body = mapper.writeValueAsString(
            EditPasswordRequestDto.builder()
                .password(password)
                .newPassword(newPassword)
                .build()
        );

        mvc.perform(patch(BASE_URL + "/password")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
            )
            .andExpect(status().isOk())
            .andDo(print());

        // when-then-i. 기존 비밀변경 입력 실패
        mvc.perform(patch(BASE_URL + "/password")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
            )
            .andExpect(status().isBadRequest())
            .andDo(print());

        // when-then-ii. 새 비밀번호를 비밀번호 형식에 맞추지 않음
        body = mapper.writeValueAsString(
            EditPasswordRequestDto.builder()
                .password(newPassword)
                .newPassword("wrongPassword")
                .build()
        );

        mvc.perform(patch(BASE_URL + "/password")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
            )
            .andExpect(status().isBadRequest())
            .andDo(print());

        // when-then-iii. 2회 이내에 사용한 이력이 있는 비밀번호 사용 시도
        body = mapper.writeValueAsString(
            EditPasswordRequestDto.builder()
                .password(newPassword)
                .newPassword(newPassword)
                .build()
        );

        mvc.perform(patch(BASE_URL + "/password")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
            )
            .andExpect(status().isBadRequest())
            .andDo(print());
    }
}
