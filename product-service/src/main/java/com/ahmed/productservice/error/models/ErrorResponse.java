package com.ahmed.productservice.error.models;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {}