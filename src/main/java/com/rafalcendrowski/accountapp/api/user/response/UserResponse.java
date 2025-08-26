package com.rafalcendrowski.accountapp.api.user.response;

import com.rafalcendrowski.accountapp.model.user.UserRole;

import java.util.List;

public record UserResponse(String id, String username, String name, String surname, List<UserRole> roles) {
}
