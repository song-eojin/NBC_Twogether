package com.example.twogether.user.entity;

import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.entity.WorkspaceMember;
import com.example.twogether.board.entity.Board;
import com.example.twogether.board.entity.BoardMember;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    /**
     * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nickname;
    private String introduction;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;
    
    /**
     * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
     */

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<UserPassword> userPasswords = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Workspace> workspaces = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<WorkspaceMember> workspaceMembers = new ArrayList<>();

    // orphanRemoval 은 테스트 코드 작성 전 수정 예정입니다.
    @Builder.Default
    @OneToMany(mappedBy = "boardAuthor", orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "boardCollabo", orphanRemoval = true)
    private List<BoardMember> boardMembers = new ArrayList<>();

    /**
     * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
     */

    /**
     * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
     */
    public void editUserInfo(String nickname, String introduction) {
        this.nickname = nickname;
        this.introduction = introduction;
    }

    public void editPassword(String newPassword) {
        this.password = newPassword;
    }
}