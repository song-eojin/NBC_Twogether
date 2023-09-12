package com.example.twogether.alarm.event;

import com.example.twogether.alarm.dto.InvitedBoardColRequestDto;
import com.example.twogether.alarm.dto.InvitedWpColRequestDto;
import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.alarm.entity.AlarmTrigger;
import com.example.twogether.alarm.service.AlarmService;
import com.example.twogether.board.entity.Board;
import com.example.twogether.workspace.entity.Workspace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvitedBoardColEventListener implements ApplicationListener<InvitedBoardColEvent> {

    private final AlarmService alarmService;

    @Override
    @TransactionalEventListener
    public void onApplicationEvent(InvitedBoardColEvent event) {

        Board board = event.getBoard();
        log.info("invitedBoardColEvent() : 보드 협업자 초대 이벤트를 확인했습니다.");

        Alarm alarm = InvitedBoardColRequestDto.toEntity(
            board.getUser(),
            event.getInvitedUser(),
            event.getTitle(),
            event.getContent(),
            "/api/workspaces/" + board.getWorkspace().getId() + "/boards/" + board.getId() + "/invite",
            AlarmTrigger.INVITED_BOARD_COLLABORATOR
        );
        alarmService.createAlarm(alarm);
    }
}
