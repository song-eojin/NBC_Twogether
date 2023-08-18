package com.example.twogether.board.entity;

import com.example.twogether.board.dto.BoardRequestDto;
import com.example.twogether.common.entity.Timestamped;
import com.example.twogether.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class Board extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User boardAuthor;

    @Column(nullable = false)
    private String title;

    @Column
    private String color;

    @Column
    private String info;

    // orphanRemoval 은 테스트 코드 작성 전 수정 예정입니다.
    @Builder.Default
    @OneToMany(mappedBy = "board", orphanRemoval = true)
    private List<BoardMember> boardMembers = new ArrayList<>();

    public void updateTitle(BoardRequestDto boardRequestDto) {
        this.title = boardRequestDto.getTitle();
    }

    public void updateColor(BoardRequestDto boardRequestDto) {
        this.color = boardRequestDto.getColor();
    }

    public void updateInfo(BoardRequestDto boardRequestDto) {
        this.info = boardRequestDto.getInfo();
    }
}
