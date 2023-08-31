package com.example.twogether.alarm.event;

import com.example.twogether.alarm.entity.AlarmTarget;
import com.example.twogether.alarm.repository.AlarmTargetRepository;
import com.example.twogether.board.entity.Board;
import com.example.twogether.card.entity.Card;
import com.example.twogether.card.entity.CardCollaborator;
import com.example.twogether.comment.entity.Comment;
import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
@RequiredArgsConstructor
public class TriggerEventPublisher {

    public final ApplicationEventPublisher eventPublisher;

    // 워크스페이스 협업자 초대
    @Async
    public void publishInviteWpColEvent(User workspaceUser, User invitedUser, Workspace workspace) {

        InvitedWpColEvent event = new InvitedWpColEvent(this, workspaceUser, invitedUser, workspace);
        eventPublisher.publishEvent(event);
    }

    // 보드 협업자 초대
    @Async
    public void publishInviteBoardColEvent(User boardUser, User invitedUser, Board board) {

        InvitedBoardColEvent event = new InvitedBoardColEvent(this, boardUser, invitedUser, board);
        eventPublisher.publishEvent(event);
    }

    // 카드에 협업자 할당
    @Async
    public void publishInvitedCardColEvent(User boardUser, User addedUser, Card card) {

        InvitedCardColEvent event = new InvitedCardColEvent(this, boardUser, addedUser, card);
        eventPublisher.publishEvent(event);
    }

    // 카드 수정
    @Async
    public void publishCardEditedEvent(User user, List<AlarmTarget> alarmTargets, Card card, String oldContent, String newContent) {

        for (AlarmTarget alarmTarget : alarmTargets) {

            User targetUser = alarmTarget.getUser();
            CardEditedEvent event = new CardEditedEvent(this, user, targetUser, card, oldContent, newContent);
            eventPublisher.publishEvent(event);
        }
    }

    // 카드 댓글 생성
    @Async
    public void publishCardCommentEvent(User user, List<AlarmTarget> alarmTargets, Card card, Comment comment) {

        for (AlarmTarget alarmTarget : alarmTargets) {

            User targetUser = alarmTarget.getUser();
            CardCommentEvent event = new CardCommentEvent(this, user, targetUser, card, comment);
            eventPublisher.publishEvent(event);
        }
    }

    // 카드 마감일 수정
    @Async
    public void publishCardEditedDueEvent(User user, List<AlarmTarget> alertTargets, Card card, LocalDateTime oldDue, LocalDateTime newDue) {

        for (AlarmTarget alarmTarget : alertTargets) {

            User targetUser = alarmTarget.getUser();
            CardEditedDueEvent event = new CardEditedDueEvent(this, user, targetUser, card, oldDue, newDue);
            eventPublisher.publishEvent(event);
        }
    }
}
