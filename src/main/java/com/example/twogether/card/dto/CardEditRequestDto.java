package com.example.twogether.card.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;


@Getter
@Setter
public class CardEditRequestDto {

    private String title;
    private String content;
}
