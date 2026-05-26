package com.example.codereviewagent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotBlank String owner,
        @NotBlank String repo,
        @NotNull Integer pullRequestNumber
) {}
