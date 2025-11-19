package com.doclarify.core.controller;

import com.doclarify.core.domain.Document;
import com.doclarify.core.service.DocumentService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService ds) {
        this.documentService = ds;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") @NotNull MultipartFile file) throws Exception {
        UUID id = documentService.saveUpload(file);
        return ResponseEntity.ok(Map.of("documentId", id, "status", "UPLOADING"));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> status(@PathVariable UUID id) {
        Document d = documentService.getDocument(id);
        return ResponseEntity.ok(Map.of("status", d.getStatus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> get(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getDocument(id));
    }
    @GetMapping("/{id}/analysis")
    public ResponseEntity<Map<String,Object>> analysis(@PathVariable UUID id) {
        Document d = documentService.getDocument(id);
        // if OCR_COMPLETE or ANALYZED return rawText/analysis
        return ResponseEntity.ok(Map.of(
                "documentId", d.getId(),
                "status", d.getStatus(),
                "rawText", d.getRawText()
        ));
    }

}
