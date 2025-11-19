package com.doclarify.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "extracted_fields")
public class ExtractedField {
    @Id
    private UUID id;

    private UUID documentId;
    private String fieldName;
    @Column(columnDefinition = "text")
    private String fieldValue;
    private Double confidence;
}
