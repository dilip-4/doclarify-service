package com.doclarify.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name ="documents")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Document {

    @Id
    private UUID id;
    private String filePath;
    private String contentType;
    private Long size;
    private String uploadedBy;
    private Instant uploadTime;
    private DocumentStatus status; // UPLOADING | PROCESSING | DONE | ERROR
    @Column(columnDefinition = "text")
    private String simpleSummary;
    @Column(columnDefinition = "text")
    private String riskSummary;
    private Double riskScore;
    private String fileName;
    private String rawText;
}
