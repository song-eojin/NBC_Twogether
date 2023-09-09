package com.example.twogether.user.entity;

import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.alarm.entity.AlarmTarget;
import com.example.twogether.board.entity.Board;
import com.example.twogether.board.entity.BoardCollaborator;
import com.example.twogether.card.entity.CardCollaborator;
import com.example.twogether.comment.entity.Comment;
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.entity.WorkspaceCollaborator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "users")
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

    private Long kakaoId;
    private String naverId;

    private String nickname;
    private String introduction;
    private String icon;

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
    private List<WorkspaceCollaborator> workspaceCollaborators = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Board> boards = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<BoardCollaborator> boardCollaborators = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<CardCollaborator> cardCollaborators = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Alarm> alarms = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<AlarmTarget> alarmTargets = new ArrayList<>();

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

    public void editRole(UserRoleEnum userRoleEnum) {
        this.role = userRoleEnum;
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public User naverIdUpdate(String naverId) {
        this.naverId = naverId;
        return this;
    }

    public void editIcon(String icon) {
        this.icon = icon;
    }
}