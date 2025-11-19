package com.doclarify.core.service;

import com.doclarify.core.domain.Document;
import com.doclarify.core.domain.DocumentStatus;
import com.doclarify.core.repository.DocumentRepository;
import com.doclarify.core.service.ocr.OcrService;
import com.doclarify.core.service.ocr.TikaOcrService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentProcessor processor;
    private final Path uploadDir;
    private final OcrService ocrService;

    public DocumentService(DocumentRepository documentRepository,
                           DocumentProcessor processor,
                           OcrService ocrService,
                           @Value("${app.upload-dir:uploads}") String uploadDir) throws Exception {
        this.documentRepository = documentRepository;
        this.processor = processor;
        this.uploadDir = Path.of(uploadDir);
        this.ocrService = ocrService;
        Files.createDirectories(this.uploadDir);
    }

    public UUID saveUpload(MultipartFile file) throws Exception {
        UUID id = UUID.randomUUID();
        String filename = file.getOriginalFilename();
        Path target = uploadDir.resolve(id + "_" + filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        Document doc = Document.builder()
                .id(id)
                .fileName(filename)
                .filePath(target.toString())
                .contentType(file.getContentType())
                .size(file.getSize())
                .uploadTime(Instant.now())
                .status(DocumentStatus.UPLOADING)
                .build();

        documentRepository.save(doc);

        // async processing
        processor.processDocumentAsync(doc);

        return id;
    }
    @Async("ocrExecutor")
    public void processOcrAsync(UUID documentId, Path path) {
        try {
            Document d = documentRepository.findById(documentId).orElseThrow();
            d.setStatus(DocumentStatus.OCR_PROCESSING);
            documentRepository.save(d);

            String text = ocrService.extractText(path);
            d.setRawText(text);
            d.setStatus(DocumentStatus.OCR_COMPLETE);
            documentRepository.save(d);
        } catch (Exception ex) {
            Document d = documentRepository.findById(documentId).orElse(null);
            if (d != null) {
                d.setStatus(DocumentStatus.FAILED);
                documentRepository.save(d);
            }
            // log error
            ex.printStackTrace();
        }
    }
    public Document getDocument(UUID id) {
        Optional<Document> op = documentRepository.findById(id);
        return op.orElseThrow(() -> new RuntimeException("Document not found"));
    }
}
