package com.doclarify.core.repository;

import com.doclarify.core.domain.Risk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RiskRepository extends JpaRepository<Risk, UUID> {
}
