package com.doclarify.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name="clauses")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Clause {
    @Id
    private UUID id;
    private UUID documentId;
    private Integer clauseIndex;
    @Column(columnDefinition = "text")
    private String text;
    private String type;
    private String severity;
    private Integer startPos;
    private Integer endPos;
}
