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
import jakarta.persistence.Table;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "board")
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

//    @Builder.Default
//    @OneToMany(mappedBy = "board", orphanRemoval = true)
//    private List<BoardUser> boardUsers = new ArrayList<>();

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
