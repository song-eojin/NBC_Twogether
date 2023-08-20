package com.example.twogether.deck.entity;

import com.example.twogether.board.entity.Board;
import com.example.twogether.common.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Deck extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private float position;

    @Builder.Default
    @Column(name = "is_archived")
    private boolean archived = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

//    @Builder.Default
//    @OneToMany(mappedBy = "deck")
//    private List<Card> cardList = new ArrayList<>();

    public void editTitle(String title) {
        this.title = title;
    }

    public void archive() {
        this.archived = !this.isArchived();
    }

    public void editPosition(float position) {
        this.position = position;
    }
}
