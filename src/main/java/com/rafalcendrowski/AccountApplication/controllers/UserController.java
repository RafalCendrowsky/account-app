package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.user.UserDto;
import com.rafalcendrowski.AccountApplication.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.loadAllUsers().stream()
                .map(UserDto::of)
                .toList();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        User user = userService.loadById(id);
        return UserDto.of(user);
    }

    @GetMapping("/find/{username}")
    public UserDto getUserByUsername(@PathVariable String username) {
        User user = userService.loadByUsername(username);
        return UserDto.of(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @AuthenticationPrincipal User admin) {
        User user = userService.loadById(id);
        if (user.hasRole(User.Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR!");
        } else {
            userService.deleteUser(user);
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/{id}/role")
    public UserDto updateRole(@PathVariable Long id, @Valid @RequestBody User.Role role, @AuthenticationPrincipal User admin) {
        User user = userService.loadById(id);
        if (user.hasRole(User.Role.ADMINISTRATOR) != role.equals(User.Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot combine ADMINISTRATOR with other roles");
        } else {
            if (user.addRole(role)) {
                userService.updateUser(user);
            }
            return UserDto.of(user);
        }
    }

    @DeleteMapping("/{id}/role")
    public UserDto deleteRole(@PathVariable Long id, @Valid @RequestBody User.Role role, @AuthenticationPrincipal User admin) {
        User user = userService.loadById(id);
        if (!user.hasRole(role)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User role not found");
        } else {
            if (role.equals(User.Role.ADMINISTRATOR)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove ADMINISTRATOR");
            } else if (user.getRoles().size() == 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove user's only role");
            }
            user.removeRole(role);
            userService.updateUser(user);
            return UserDto.of(user);
        }
    }
}