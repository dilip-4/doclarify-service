package com.doclarify.core.service;

import com.doclarify.core.domain.Clause;
import com.doclarify.core.domain.Document;
import com.doclarify.core.domain.DocumentStatus;
import com.doclarify.core.domain.Risk;
import com.doclarify.core.repository.ClauseRepository;
import com.doclarify.core.repository.DocumentRepository;
import com.doclarify.core.repository.RiskRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentProcessor {

    private final DocumentRepository documentRepository;
    private final ClauseRepository clauseRepository;
    private final RiskRepository riskRepository;
    private final RuleEngine ruleEngine;

    public DocumentProcessor(DocumentRepository documentRepository,
                             ClauseRepository clauseRepository,
                             RiskRepository riskRepository,
                             RuleEngine ruleEngine) {
        this.documentRepository = documentRepository;
        this.clauseRepository = clauseRepository;
        this.riskRepository = riskRepository;
        this.ruleEngine = ruleEngine;
    }

    @Async("taskExecutor")
    public void processDocumentAsync(Document doc) {
        try {
            doc.setStatus(DocumentStatus.PROCESSING);
            documentRepository.save(doc);

            String text = extractText(new File(doc.getFilePath()));
            List<String> clauses = splitClauses(text);

            int index = 0;
            for (String c : clauses) {
                Clause clause = Clause.builder()
                        .id(UUID.randomUUID())
                        .documentId(doc.getId())
                        .clauseIndex(index++)
                        .text(c)
                        .build();
                clauseRepository.save(clause);
            }

            // Run rule engine to detect risks
            List<Risk> risks = ruleEngine.runRules(doc.getId(), clauses);
            for (Risk r : risks) {
                riskRepository.save(r);
            }

            // Summaries (naive)
            String simpleSummary = clauses.stream().limit(5).collect(Collectors.joining("\n\n"));
            String riskSummary = risks.isEmpty() ? "No immediate risks detected" :
                    risks.stream().map(r -> r.getRiskType() + ":" + r.getSeverity()).collect(Collectors.joining("; "));

            doc.setSimpleSummary(simpleSummary);
            doc.setRiskSummary(riskSummary);
            doc.setStatus(DocumentStatus.DONE);
            doc.setUploadTime(Instant.now());
            documentRepository.save(doc);

        } catch (Exception e) {
            doc.setStatus(DocumentStatus.FAILED);
            documentRepository.save(doc);
            e.printStackTrace();
        }
    }

    private String extractText(File file) throws IOException, InterruptedException {
        String path = file.getAbsolutePath().toLowerCase();
        if (path.endsWith(".pdf")) {
            try (PDDocument p = PDDocument.load(file)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(p);
            }
        }
        // fallback to tesseract for images or scanned PDFs (simple approach)
        ProcessBuilder pb = new ProcessBuilder("tesseract", path, "stdout");
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process p = pb.start();
        p.waitFor();
        return new String(p.getInputStream().readAllBytes());
    }

    private List<String> splitClauses(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        // Very simple splitting heuristics:
        // split on double newline or numbered headings like "1.", "1.1", or all caps headings
        String[] parts = text.split("(?m)\\n\\s*\\n|(?m)^\\d+\\.|(?m)^\\s*[A-Z ]{3,}\\s*$");
        return java.util.Arrays.stream(parts)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
