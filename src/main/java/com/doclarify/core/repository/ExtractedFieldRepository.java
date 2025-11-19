package com.doclarify.core.repository;

import com.doclarify.core.domain.ExtractedField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface  ExtractedFieldRepository extends JpaRepository<ExtractedField, UUID> {
}
