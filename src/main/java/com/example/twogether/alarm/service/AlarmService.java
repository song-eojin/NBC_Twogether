package com.example.twogether.alarm.service;

import com.example.twogether.alarm.dto.CardEditedsResponseDto;
import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.alarm.repository.AlarmRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    @Transactional
    public void createAlarm(Alarm alarm) {
        alarmRepository.save(alarm);
    }

    @Transactional
    public void deleteAlarm(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId).orElseThrow(() ->
            new CustomException(CustomErrorCode.ALARM_NOT_FOUND));

        alarmRepository.delete(alarm);
    }

    @Transactional
    public void readAlarm(Long alarmId) {
        Alarm alarm = alarmRepository.findById(alarmId).orElseThrow(() ->
            new CustomException(CustomErrorCode.ALARM_NOT_FOUND));

        alarm.read();
    }

    @Transactional(readOnly = true)
    public CardEditedsResponseDto getAlarms(User user) {

        List<Alarm> alarms = alarmRepository.findAllByUser(user);
        return CardEditedsResponseDto.of(alarms);
    }
}
