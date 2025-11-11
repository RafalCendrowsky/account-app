package com.rafalcendrowski.accountapp.api.user.request;

import com.rafalcendrowski.accountapp.persistance.user.UserRole;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UserRoleRequest(@NotEmpty Set<UserRole> roles) {
}
