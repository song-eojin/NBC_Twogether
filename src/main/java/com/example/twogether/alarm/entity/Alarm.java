package com.example.twogether.alarm.entity;

import com.example.twogether.common.entity.Timestamped;
import com.example.twogether.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm extends Timestamped {

    /*공통 필드*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    private String url; // 알림의 대상이 되는 페이지로 이동

    @Column(nullable = false)
    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmTrigger alarmTrigger; // 알림의 원인이 되는 event

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user; // 알림을 받는 로그인 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User eventMaker; // event를 만든 사람


    /*Collaborator 공통 필드*/
    private Long wpId;
    private String wpTitle;
    private Long boardId;
    private String boardTitle;


    /*Added Card Collaborator Event*/
    private Long cardId;
    private String cardTitle;


    /*CardEdited Event*/
    @Builder.Default
    @Column
    private Boolean isRead = false;


    public void read(){
        this.isRead = true;
    }
}
