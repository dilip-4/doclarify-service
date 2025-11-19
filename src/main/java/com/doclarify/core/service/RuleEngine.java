package com.doclarify.core.service;

import com.doclarify.core.domain.Risk;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RuleEngine {

    /**
     * Very simple rule runner. Real rules should be config-driven (YAML/DB) and more sophisticated.
     * For MVP we implement two rules:
     *  - NO_TERMINATION: if no clause contains 'termination'
     *  - AUTO_RENEWAL: if document contains 'auto renewable' or 'auto-renew'
     */
    public List<Risk> runRules(UUID documentId, List<String> clauses) {
        List<Risk> out = new ArrayList<>();

        boolean hasTermination = clauses.stream().anyMatch(s -> s.toLowerCase().contains("termination"));
        if (!hasTermination) {
            out.add(Risk.builder()
                    .id(UUID.randomUUID())
                    .documentId(documentId)
                    .clauseId(null)
                    .riskType("NO_TERMINATION")
                    .severity("HIGH")
                    .message("No termination clause detected")
                    .ruleId("R_NO_TERMINATION")
                    .createdAt(Instant.now())
                    .build());
        }

        boolean hasAutoRenew = clauses.stream().anyMatch(s ->
                s.toLowerCase().contains("auto-renew") || s.toLowerCase().contains("auto renew") ||
                        s.toLowerCase().contains("renewal automatically"));
        if (hasAutoRenew) {
            out.add(Risk.builder()
                    .id(UUID.randomUUID())
                    .documentId(documentId)
                    .clauseId(null)
                    .riskType("AUTO_RENEWAL")
                    .severity("MEDIUM")
                    .message("Document appears to contain auto-renewal language")
                    .ruleId("R_AUTO_RENEWAL")
                    .createdAt(Instant.now())
                    .build());
        }

        return out;
    }
}
