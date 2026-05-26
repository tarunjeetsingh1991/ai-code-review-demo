package com.example.codereviewagent.dto;

public record ChangedFile(
        String filename,
        String status,
        String patch
) {}
