package com.example.twogether.alarm.repository;

import com.example.twogether.alarm.entity.Alarm;
import com.example.twogether.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findAllByUser(User user);
}
