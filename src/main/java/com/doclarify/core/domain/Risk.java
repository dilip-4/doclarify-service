package com.doclarify.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "risks")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Risk {
    @Id
    private UUID id;

    private UUID documentId;
    private UUID clauseId;
    private String riskType;
    private String severity;
    @Column(columnDefinition = "text")
    private String message;
    private String ruleId;
    private Instant createdAt;
}
