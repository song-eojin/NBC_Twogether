package com.example.twogether.workspace.repository;

import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WpRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findAllByUserOrderByCreatedAtDesc(User user);
    List<Workspace> findAllByUser_Id(Long id);
    List<Workspace> findAllByWorkspaceCollaborators_Email(String email);
    Optional<Workspace> findByIdAndWorkspaceCollaborators_Email(Long wpId, String email);
    //Optional<Workspace> findByIdWithCollaborators(Long wpId);
}
