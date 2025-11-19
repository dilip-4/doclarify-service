package com.doclarify.core.service.ocr;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class TikaOcrService implements OcrService {

    private final Tika tika = new Tika();

    @Override
    public String extractText(Path inputFile) throws Exception {
        // For most DOCX/PDF that are text-based, Tika is sufficient.
        try {
            String text = tika.parseToString(inputFile.toFile());
            text = text == null ? "" : text.trim();
            if (text.length() > 50) {
                return text;
            }
        } catch (TikaException te) {
            System.out.println("Tika OCR Service extract text caught exception");
        }

        // fallback: if it's a PDF that might be scanned, try OCR via Tesseract
        return tesseractFallback(inputFile);
    }

    private String tesseractFallback(Path inputFile) throws Exception {
        // Simple fallback: call `tesseract` CLI if available.
        // For production use, use tess4j or a cloud OCR service.
        Path out = inputFile.getParent().resolve(inputFile.getFileName().toString() + ".txt");
        ProcessBuilder pb = new ProcessBuilder("tesseract",
                inputFile.toAbsolutePath().toString(),
                out.toAbsolutePath().toString().replaceFirst("\\.txt$", ""));
        pb.redirectErrorStream(true);
        Process p = pb.start();
        int rc = p.waitFor();
        if (rc == 0 && out.toFile().exists()) {
            return java.nio.file.Files.readString(out);
        }
        return "";
    }
}
