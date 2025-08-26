package com.rafalcendrowski.accountapp.api.user;

import com.rafalcendrowski.accountapp.api.user.request.UserRoleRequest;
import com.rafalcendrowski.accountapp.api.user.response.UserResponse;
import com.rafalcendrowski.accountapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@PreAuthorize("hasRole('ADMINISTRATOR')")
@RequiredArgsConstructor
public class UserController {
    final UserService userService;

    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.loadAllUsers();
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable String id) {
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.delete(id);

    }

    @PutMapping("/{id}/role")
    public UserResponse updateRoles(@PathVariable String id, @Valid @RequestBody UserRoleRequest request) {
        return userService.updateUserRoles(id, request);
    }

    @GetMapping("/find/{username}")
    public UserResponse getUserByUsername(@PathVariable String username) {
        return userService.getByUsername(username);
    }
}