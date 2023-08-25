package com.example.twogether.board.entity;

import com.example.twogether.board.dto.BoardRequestDto;
import com.example.twogether.common.entity.Timestamped;
import com.example.twogether.deck.entity.Deck;
import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.entity.WorkspaceCollaborator;
import jakarta.persistence.CascadeType;
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

    @Column(nullable = false)
    private String title;

    @Column
    private String color;

    @Column
    private String info;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "board")
    private List<BoardCollaborator> boardCollaborators = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Deck> decks = new ArrayList<>();

    public void editTitle(BoardRequestDto boardRequestDto) {
        this.title = boardRequestDto.getTitle();
    }

    public void editColor(BoardRequestDto boardRequestDto) {
        this.color = boardRequestDto.getColor();
    }

    public void editInfo(BoardRequestDto boardRequestDto) {
        this.info = boardRequestDto.getInfo();
    }
}
