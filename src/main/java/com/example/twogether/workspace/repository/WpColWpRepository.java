package com.example.twogether.workspace.repository;

import com.example.twogether.workspace.entity.WorkspaceCollaborator;
import com.example.twogether.workspace.entity.WpColWp;
import com.example.twogether.workspace.entity.WpColWpId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WpColWpRepository extends JpaRepository<WpColWp, WpColWpId> {
    Optional<WorkspaceCollaborator> findByWorkspace_IdAndWorkspaceCollaborator_Email(Long wpId, String email);
}
