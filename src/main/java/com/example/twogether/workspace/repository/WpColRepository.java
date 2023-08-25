package com.example.twogether.workspace.repository;

import com.example.twogether.user.entity.User;
import com.example.twogether.workspace.entity.Workspace;
import com.example.twogether.workspace.entity.WorkspaceCollaborator;
import java.util.List;
import java.util.Optional;
import org.hibernate.jdbc.Work;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WpColRepository extends JpaRepository<WorkspaceCollaborator, Long> {
    boolean existsByWorkspaceAndEmail(Workspace workspace, String email);
    Optional<WorkspaceCollaborator> findByWorkspaceAndEmail(Workspace workspace, String email);
    void deleteAllByWorkspace_Id(Long id);
    List<WorkspaceCollaborator> findByUser(User user);
}