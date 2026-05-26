package com.example.codereviewagent.dto;

public record Finding(
        String severity,
        String file,
        String title,
        String details,
        boolean createJira
) {}
