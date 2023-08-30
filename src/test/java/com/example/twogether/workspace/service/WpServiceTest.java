package com.example.twogether.workspace.service;


import static org.mockito.ArgumentMatchers.anyLong;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.repository.UserRepository;
import com.example.twogether.workspace.dto.WpRequestDto;
import com.example.twogether.workspace.dto.WpResponseDto;
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.repository.WpRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WpServiceTest {

    @Autowired
    private WpService wpService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WpRepository wpRepository;
    private User user1;
    private User user2;
    private User user3;


    @BeforeEach
    void signUp() {
        user1 = userRepository.findById(1L).orElse(null);
        user2 = userRepository.findById(2L).orElse(null);
        user3 = userRepository.findById(3L).orElse(null);
    }


    @Test
    @DisplayName("워크스페이스 생성 테스트")
    public void createWorkspace() {
        String title = "Two Gether 1 !";
        String icon = "Two Gether 1 - icon 1 !";

        WpRequestDto wpRequestDto = WpRequestDto.builder()
            .title(title)
            .icon(icon)
            .build();

        // when
        WpResponseDto savedWp = wpService.createWorkspace(user1, wpRequestDto);
        List<Workspace> workspaces = wpRepository.findAll();

        // then
        Assertions.assertEquals(title, savedWp.getTitle());
        Assertions.assertEquals(icon, savedWp.getIcon());
        Assertions.assertEquals(user1.getId(), workspaces.get(workspaces.size()-1).getUser().getId());
    }

    @Test
    @DisplayName("워크스페이스 수정 테스트")
    void editWorkspace() {
        //given
        List<Workspace> workspaces = wpRepository.findAll();
        Workspace workspace = workspaces.get(0);
        String editedTitle = "Two Gether 1 - 새 워크스페이스 1 !";
        String editedIcon = "Two Gether 1 - icon 1 - 새 아이콘 1 !";

        WpRequestDto wpRequestDto = WpRequestDto.builder()
            .title(editedTitle)
            .icon(editedIcon)
            .build();

        // when
        WpResponseDto editedWp = wpService.editWorkspace(user1, workspace.getId(), wpRequestDto);

        // then
        Assertions.assertEquals(editedTitle, editedWp.getTitle());
        Assertions.assertEquals(editedIcon, editedWp.getIcon());
    }

    @Test
    @DisplayName("워크스페이스 수정 실패 테스트 1")
    void editFailWorkspace1() {
        //given
        List<Workspace> workspaces = wpRepository.findAll();
        Workspace workspace = workspaces.get(2);
        String editedTitle = "Two Gether 1 - 새 워크스페이스 1 !";
        String editedIcon = "Two Gether 1 - icon 1 - 새 아이콘 1 !";

        WpRequestDto wpRequestDto = WpRequestDto.builder()
            .title(editedTitle)
            .icon(editedIcon)
            .build();

        // when
        try {
            wpService.editWorkspace(user1, workspace.getId(), wpRequestDto);
        } catch (CustomException e) {
            Assertions.assertEquals(CustomErrorCode.NOT_YOUR_WORKSPACE, e.getErrorCode());
        }
    }

    @Test
    @DisplayName("워크스페이스 수정 실패 테스트 2 - 존재하지 않는 wpId(워크스페이스 ID) 수정 테스트")
    void editFailWorkspace2() {
        //given
        Long wpId = 4L;
        String editedTitle = "Two Gether 1 - 새 워크스페이스 1 !";
        String editedIcon = "Two Gether 1 - icon 1 - 새 아이콘 1 !";

        WpRequestDto wpRequestDto = WpRequestDto.builder()
            .title(editedTitle)
            .icon(editedIcon)
            .build();

        // when
        try {
            wpService.editWorkspace(user1, wpId, wpRequestDto);
        } catch (CustomException e) {
            Assertions.assertEquals(CustomErrorCode.WORKSPACE_NOT_FOUND, e.getErrorCode());
        }
    }

    @Test
    @DisplayName("워크스페이스 수정 실패 테스트 3 - 다른 유저가 생성한 워크스페이스를 수정 테스트")
    void editFailWorkspace3() {
        //given
        Long wpId = 1L;
        String editedTitle = "Two Gether 1 - 새 워크스페이스 1 !";
        String editedIcon = "Two Gether 1 - icon 1 - 새 아이콘 1 !";

        WpRequestDto wpRequestDto = WpRequestDto.builder()
            .title(editedTitle)
            .icon(editedIcon)
            .build();

        // when
        try {
            wpService.editWorkspace(user2, wpId, wpRequestDto);
        } catch (CustomException e) {
            Assertions.assertEquals(CustomErrorCode.NOT_YOUR_WORKSPACE, e.getErrorCode());
        }
    }

    @Test
    @DisplayName("워크스페이스 삭제 테스트")
    void deleteWorkspace() {
        //given
        Long wpId = 1L;
        List<Workspace> workspaces = wpRepository.findAll();

        // when
        wpService.deleteWorkspace(user1, wpId);
        List<Workspace> DeletedWps = wpRepository.findAll();

        // then
        Assertions.assertNull(wpRepository.findById(anyLong()).orElse(null));

        // 1번 워크스페이스 삭제 시 남아있는 보드 전체가 DB에 정상적으로 있는지 확인
        for (int i = 0; i < workspaces.size(); i++) {
            if (!workspaces.get(i).getId().equals(wpId)) {
                Workspace workspace = workspaces.get(i);
                Workspace DeletedWp = DeletedWps.get(i-1);
                Assertions.assertEquals(workspace.getTitle(), DeletedWp.getTitle());
                Assertions.assertEquals(workspace.getIcon(), DeletedWp.getIcon());
            }
        }
    }

    @Test
    @DisplayName("워크스페이스 삭제 실패 테스트 1 - 존재 하지 않는 워크스페이스 삭제 테스트")
    void deleteFailWorkspace1() {
        //given
        Long wpId = 4L;
        List<Workspace> workspaces = wpRepository.findAll();

        try {
            wpService.deleteWorkspace(user1, wpId);
        } catch (CustomException e) {
            Assertions.assertEquals(CustomErrorCode.WORKSPACE_NOT_FOUND, e.getErrorCode());
        }

        List<Workspace> DeletedWps = wpRepository.findAll();

        for (int i = 0; i < workspaces.size(); i++) {
            if (!workspaces.get(i).getId().equals(wpId)) {
                Workspace workspace = workspaces.get(i);
                Workspace DeletedWp = DeletedWps.get(i);

                Assertions.assertEquals(workspace.getTitle(), DeletedWp.getTitle());
                Assertions.assertEquals(workspace.getIcon(), DeletedWp.getIcon());
            }
        }
    }

    @Test
    @DisplayName("워크스페이스 삭제 실패 테스트 2 - 다른 유저가 생성한 워크스페이스 삭제 테스트")
    void deleteFailWorkspace2() {
        //given
        Long wpId = 3L;
        List<Workspace> workspaces = wpRepository.findAll();

        try {
            wpService.deleteWorkspace(user1, wpId);
        } catch (CustomException e) {
            Assertions.assertEquals(CustomErrorCode.NOT_YOUR_WORKSPACE, e.getErrorCode());
        }

        List<Workspace> DeletedWps = wpRepository.findAll();

        for (int i = 0; i < workspaces.size(); i++) {
            if (!workspaces.get(i).getId().equals(wpId)) {
                Workspace workspace = workspaces.get(i);
                Workspace DeletedWp = DeletedWps.get(i);

                Assertions.assertEquals(workspace.getTitle(), DeletedWp.getTitle());
                Assertions.assertEquals(workspace.getIcon(), DeletedWp.getIcon());
            }
        }
    }

    @Test
    @DisplayName("워크스페이스 단건 조회 테스트")
    void getWorkspace() {
        //given
        List<Workspace> workspaces = wpRepository.findAll();

        // when
        WpResponseDto wpResponseDto1 = wpService.getWorkspace(workspaces.get(0).getId());
        WpResponseDto wpResponseDto2 = wpService.getWorkspace(workspaces.get(1).getId());
        WpResponseDto wpResponseDto3 = wpService.getWorkspace(workspaces.get(2).getId());


        // then
        Assertions.assertNull(wpRepository.findById(anyLong()).orElse(null));
        Assertions.assertEquals("Workspace 1", wpResponseDto1.getTitle());
        Assertions.assertEquals("test", wpResponseDto1.getIcon());
        Assertions.assertEquals("Workspace 2", wpResponseDto2.getTitle());
        Assertions.assertEquals("Workspace 1", wpResponseDto3.getTitle());

    }

    @Test
    @DisplayName("워크스페이스 단건 조회 실패 테스트 - 존재하지 않는 워크스페이스 조회 테스트")
    void getFailWorkspace() {
        Long wpId = 4L;

        try {
            wpService.getWorkspace(wpId);
        } catch (CustomException e) {
            Assertions.assertEquals(CustomErrorCode.WORKSPACE_NOT_FOUND, e.getErrorCode());
        }
    }

    @Test
    @DisplayName("워크스페이스 전체 조회")
    public void getWorkspaces() {
        //given
        // 전체 워크스페이스 조회
        List<Workspace> workspaces = wpRepository.findAll();

        // 특정 user의 워크스페이스 전체 조회
        Long userId = 1L;
        List<Workspace> user1wps = wpRepository.findAllByUser_Id(userId);

        // when
        wpService.getWorkspaces(user1);

        // then
        Assertions.assertEquals(3, workspaces.size());
        Assertions.assertEquals(2, user1wps.size());
    }
}
