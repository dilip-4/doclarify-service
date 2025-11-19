package com.doclarify.core.repository;

import com.doclarify.core.domain.Clause;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClauseRepository extends JpaRepository<Clause, UUID> {

    List<Clause> findByDocumentIdOrderByClauseIndex(UUID id);
}
