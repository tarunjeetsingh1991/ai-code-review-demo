package com.example.codereviewagent.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class SpecificationService {

    public String loadReviewSpecification() {
        try {
            ClassPathResource resource = new ClassPathResource("specs/review-spec.md");
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return "No specification file found. Use default Spring Boot code review rules.";
        }
    }
}
