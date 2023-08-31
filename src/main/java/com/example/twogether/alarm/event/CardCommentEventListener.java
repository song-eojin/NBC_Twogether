package com.example.twogether.alarm.event;

import com.example.twogether.alarm.dto.CardExtraRequestDto;
import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.alarm.entity.AlarmTrigger;
import com.example.twogether.alarm.service.AlarmService;
import com.example.twogether.card.entity.Card;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardCommentEventListener implements ApplicationListener<CardCommentEvent> {

    private final AlarmService alarmService;

    @Override
    @TransactionalEventListener
    public void onApplicationEvent(CardCommentEvent event) {

        Card card = event.getCard();
        log.info("cardCommentEvent() : 작업자로 할당된 카드에 댓글이 생성되었습니다.");

        Alarm alarm = CardExtraRequestDto.toEntity(
            event.getEditor(),
            event.getAlarmTarget(),
            event.getContent(),
            "/api/boards/" + card.getDeck().getBoard().getId() + "/cards/" + card.getId() + "/comments",
            AlarmTrigger.CARD_COMMENT_CREATE_EVENT
        );
        alarmService.createAlarm(alarm);

    }
}
