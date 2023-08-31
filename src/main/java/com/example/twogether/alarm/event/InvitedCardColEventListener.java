package com.example.twogether.alarm.event;

import com.example.twogether.alarm.dto.InvitedBoardColRequestDto;
import com.example.twogether.alarm.dto.InvitedCardColRequestDto;
import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.alarm.entity.AlarmTrigger;
import com.example.twogether.alarm.service.AlarmService;
import com.example.twogether.board.entity.Board;
import com.example.twogether.card.entity.Card;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvitedCardColEventListener implements ApplicationListener<InvitedCardColEvent> {

    private final AlarmService alarmService;

    @Override
    @TransactionalEventListener
    public void onApplicationEvent(InvitedCardColEvent event) {

        Card card = event.getCard();
        log.info("invitedCardColEvent() : 카드에 협업자로 할당되었습니다.");

        Alarm alarm = InvitedCardColRequestDto.toEntity(
            event.getLoginUser(),
            event.getAddedUser(),
            event.getContent(),
            "/api/cards/" + card.getId() + "/users",
            AlarmTrigger.ADDED_CARD_COLLABORATOR,
            card.getId(),
            card.getTitle()
        );
        alarmService.createAlarm(alarm);
    }
}
