package com.example.codereviewagent.service;

import com.example.codereviewagent.dto.ChangedFile;
import com.example.codereviewagent.dto.Finding;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RuleBasedReviewEngine {

    public List<Finding> review(List<ChangedFile> files, String specification) {
        List<Finding> findings = new ArrayList<>();

        for (ChangedFile file : files) {
            String name = file.filename() == null ? "" : file.filename();
            String patch = file.patch() == null ? "" : file.patch();

            if (patch.contains("password") || patch.contains("secret") || patch.contains("apiKey")) {
                findings.add(new Finding(
                        "CRITICAL",
                        name,
                        "Possible secret or credential found",
                        "The patch contains sensitive keywords like password, secret, or apiKey. Verify that no real credential is committed.",
                        true
                ));
            }

            if (name.contains("Controller") && patch.contains("new ") && patch.contains("Repository")) {
                findings.add(new Finding(
                        "MAJOR",
                        name,
                        "Controller appears to access repository directly",
                        "Controller classes should call service layer methods. Repository access should stay inside service/data layer.",
                        true
                ));
            }

            if (name.contains("Controller") && !patch.contains("@Valid") && patch.contains("@RequestBody")) {
                findings.add(new Finding(
                        "MEDIUM",
                        name,
                        "Request body is missing validation",
                        "A REST API that accepts @RequestBody should usually validate DTOs using @Valid and Jakarta validation annotations.",
                        false
                ));
            }

            if (name.contains("Service") && patch.contains("save(") && !patch.contains("@Transactional")) {
                findings.add(new Finding(
                        "MAJOR",
                        name,
                        "Database write may need @Transactional",
                        "Service methods that perform database writes should be reviewed for transactional safety.",
                        true
                ));
            }

            if (name.endsWith(".java") && patch.contains("System.out.println")) {
                findings.add(new Finding(
                        "LOW",
                        name,
                        "Use logger instead of System.out.println",
                        "Replace System.out.println with SLF4J Logger for production code.",
                        false
                ));
            }

            if (name.endsWith(".java") && patch.contains("catch (Exception") && !patch.contains("throw")) {
                findings.add(new Finding(
                        "MEDIUM",
                        name,
                        "Generic exception catch may hide errors",
                        "Avoid swallowing generic exceptions. Return proper errors or rethrow domain exceptions.",
                        false
                ));
            }
        }

        if (findings.isEmpty()) {
            findings.add(new Finding(
                    "INFO",
                    "N/A",
                    "No major issues found",
                    "The changed files passed the demo rule-based review. This does not replace human review.",
                    false
            ));
        }

        return findings;
    }
}
