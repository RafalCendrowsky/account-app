package com.rafalcendrowski.accountapp.api.auth.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(@NotNull String username, @NotNull String password) {
}
