package com.example.twogether.user.repository;

import com.example.twogether.user.entity.UserPassword;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPasswordRepository extends JpaRepository<UserPassword, Long> {

    void deleteAllByUser_Id(Long id);

    List<UserPassword> findAllByUser_IdOrderByCreatedAt(Long id);
}
