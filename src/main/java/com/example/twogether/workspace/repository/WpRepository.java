package com.example.twogether.workspace.repository;

import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WpRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findAllByUserOrderByCreatedAtDesc(User user);
}
