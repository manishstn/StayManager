package com.statymanger.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDTO(@NotBlank(message = "Email is required") String email,
                              @NotBlank(message = "Password is required") String password) {
}