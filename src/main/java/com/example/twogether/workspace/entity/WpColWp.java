package com.example.twogether.workspace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// 중간 테이블
@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class WpColWp implements Persistable<WpColWpId> {
    // 복합키 매핑
    @EmbeddedId
    private WpColWpId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", insertable=false, updatable=false)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_collaborator_id", insertable=false, updatable=false)
    private WorkspaceCollaborator workspaceCollaborator;

    @CreatedDate
    private LocalDate created;

    @Override
    public WpColWpId getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return created == null;
    }
}