package com.doclarify.core.domain;

public enum DocumentStatus {
    UPLOADING,
    UPLOADED,
    OCR_PROCESSING,
    OCR_COMPLETE,
    NLP_PROCESSING,
    ANALYZED,
    FAILED,
    PROCESSING,
    DONE
}
