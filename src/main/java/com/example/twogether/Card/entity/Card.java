package com.example.twogether.Card.entity;

import com.example.twogether.Card.dto.CardEditRequestDto;
import com.example.twogether.board.entity.Board;
import com.example.twogether.common.entity.Timestamped;
import com.example.twogether.deck.entity.Deck;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
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
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card extends Timestamped {

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

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column
    private String description;

    @Column
    private String attachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id")
    private Deck deck;

//    @OneToMany(mappedBy = "card")
//    private List<Comment> commentList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "card")
//    private List<CheckList> checkLists = new ArrayList<>();
//
//    @OneToMany(mappedBy = "card")
//    private List<CardLabel> cardLabelList = new ArrayList<>();

    public void editTitle(String title) {
        this.title = title;
    }

    public void editDescription(String description) {
        this.description = description;
    }
}
