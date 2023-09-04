package com.example.twogether.user.repository;

import com.example.twogether.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByKakaoId(Long kakaoId);

    Optional<User> findByNaverId(String naverId);
}
