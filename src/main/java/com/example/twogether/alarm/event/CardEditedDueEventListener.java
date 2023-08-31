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
public class CardEditedDueEventListener implements ApplicationListener<CardEditedDueEvent> {

    private final AlarmService alarmService;

    @Override
    @TransactionalEventListener
    public void onApplicationEvent(CardEditedDueEvent event) {

        Card card = event.getCard();
        log.info("cardEditedDueEvent() : 작업자로 할당된 카드의 마감일이 변경되었습니다.");

        Alarm alarm = CardExtraRequestDto.toEntity(
            event.getEditor(),
            event.getAlarmTarget(),
            event.getContent(),
            "/api/cards/" + card.getId() + "/date",
            AlarmTrigger.CARD_EDITED_DUE_EVENT
        );
        alarmService.createAlarm(alarm);
    }
}
