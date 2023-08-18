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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Deck extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private float position;

    @Column(name = "is_deleted")
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

//    @OneToMany(mappedBy = "deck")
//    private List<Card> cardList = new ArrayList<>();

    public Deck(String title, float position, Board board) {
        this.title = title;
        this.position = position;
        this.board = board;
    }

    public void editTitle(String title) {
        this.title = title;
    }

    public void archive() {
        this.deleted = !this.isDeleted();
    }

    public void editPosition(float position) {
        this.position = position;
    }
}
