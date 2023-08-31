package com.example.twogether.alarm.repository;

import com.example.twogether.alarm.entity.AlarmTarget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmTargetRepository extends JpaRepository<AlarmTarget, Long> {
}
