package com.example.twogether.alarm.event;

import com.example.twogether.alarm.dto.CardEditedRequestDto;
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
public class CardEditedEventListener implements ApplicationListener<CardEditedEvent> {

    private final AlarmService alarmService;

    @Override
    @TransactionalEventListener
    public void onApplicationEvent(CardEditedEvent event) {

        Card card = event.getCard();
        if (!event.getOldContent().equals(event.getNewContent())) {
            log.info("cardEditedEvent() : 카드 내용이 수정된 이벤트를 확인했습니다.");

            Alarm alarm = CardEditedRequestDto.toEntity(
                event.getUser(),
                event.getAlarmTarget(),
                event.getContent(),
                "/api/cards/" + card.getId(),
                AlarmTrigger.CARD_EDITED_EVENT,
                false
            );
            alarmService.createAlarm(alarm);
        }
    }
}
