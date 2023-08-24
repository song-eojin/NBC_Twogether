package com.example.twogether.workspace.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// 식별자 클래스
@Builder
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class WpColWpId implements Serializable {
    @Column(name = "workspace_id")
    private Long wpId;

    @Column(name = "workspace_collaborator_id")
    private Long wpColId;

    public WpColWpId(Long wpId, Long wpColId) {
        this.wpId = wpId;
        this.wpColId = wpColId;
    }
}

