package com.example.twogether.alarm.event;

import com.example.twogether.alarm.dto.InvitedWpColRequestDto;
import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.alarm.entity.AlarmTrigger;
import com.example.twogether.alarm.service.AlarmService;
import com.example.twogether.workspace.entity.Workspace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvitedWpColEventListener implements ApplicationListener<InvitedWpColEvent> {

    private final AlarmService alarmService;

    @Override
    @TransactionalEventListener
    public void onApplicationEvent(InvitedWpColEvent event) {

        Workspace workspace = event.getWorkspace();
        log.info("invitedWpColEvent() : 워크스페이스 협업자 초대 이벤트를 확인했습니다.");

        Alarm alarm = InvitedWpColRequestDto.toEntity(
            workspace.getUser(),
            event.getInvitedUser(),
            event.getContent(),
            "/api/workspaces/" + workspace.getId() + "/invite",
            AlarmTrigger.INVITED_WORKSPACE_COLLABORATOR,
            workspace.getId(),
            workspace.getTitle()
        );
        alarmService.createAlarm(alarm);
    }
}