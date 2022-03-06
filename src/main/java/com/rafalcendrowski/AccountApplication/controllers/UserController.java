package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.logging.LoggerConfig;
import com.rafalcendrowski.AccountApplication.user.UserDto;
import com.rafalcendrowski.AccountApplication.user.UserService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.*;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    Logger secLogger;

    @GetMapping
    public List<UserDto> getUsers() {
        List<UserDto> userList = new ArrayList<>();
        userService.loadAllUsers().forEach(
                user -> userList.add(UserDto.of(user)));
        return userList;
    }

    @GetMapping("/{email}")
    public UserDto getUser(@PathVariable String email) {
        User user = userService.loadByUsername(email);
        return UserDto.of(user);
    }

    @DeleteMapping("/{email}")
    public Map<String, String> deleteUser(@PathVariable String email, @AuthenticationPrincipal User admin) {
        User user = userService.loadByUsername(email);
        if (user.hasRole(User.Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR!");
        } else {
            userService.deleteUser(user);
            secLogger.info(LoggerConfig.getEventLogMap(admin.getUsername(), email,
                    "DELETE_USER", "/api/admin/user"));
            return Map.of("status", "Deleted successfully", "user", email);
        }
    }

    @PutMapping("/{email}/role")
    public UserDto updateRole(@PathVariable String email, @Valid @RequestBody User.Role role, @AuthenticationPrincipal User admin) {
        User user = userService.loadByUsername(email);
        if (user.hasRole(User.Role.ADMINISTRATOR) != role.equals(User.Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot combine ADMINISTRATOR with other roles");
        } else {
            if (user.addRole(role)) {
                userService.updateUser(user);
                secLogger.info(LoggerConfig.getEventLogMap(admin.getUsername(), "Grant role %s to %s".formatted(role, role),
                        "GRANT_ROLE", "/api/admin/user/role"));
            }
            return UserDto.of(user);
        }
    }

    @DeleteMapping("/{email}/role")
    public UserDto deleteRole(@PathVariable String email, @Valid @RequestBody User.Role role, @AuthenticationPrincipal User admin) {
        User user = userService.loadByUsername(email);
        if (!user.hasRole(role)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User role not found");
        } else {
            if (role.equals(User.Role.ADMINISTRATOR)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove ADMINISTRATOR");
            } else if (user.getRoles().size() == 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove user's only role");
            }
            user.getRoles().remove(role);
            userService.updateUser(user);
            secLogger.info(LoggerConfig.getEventLogMap(admin.getUsername(), "Remove role %s from %s".formatted(role, email),
                    "REMOVE_ROLE", "/api/admin/user/role"));
            return UserDto.of(user);
        }
    }
}