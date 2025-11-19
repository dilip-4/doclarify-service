package com.doclarify.core.service.ocr;

import java.nio.file.Path;

public interface OcrService {

    String extractText(Path inputFile) throws Exception;
}
