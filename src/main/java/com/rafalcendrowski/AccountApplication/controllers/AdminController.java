package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.logging.LoggerConfig;
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
@RequestMapping("api/admin")
public class AdminController {

    @Autowired
    UserService userService;

    @Autowired
    Logger secLogger;

    @GetMapping("/user")
    public List<Map<String, Object>> getUsers() {
        List<Map<String, Object>> userList = new ArrayList<>();
        userService.loadAllUsers().forEach(
                user -> userList.add(user.getUserMap()));
        return userList;
    }

    @DeleteMapping("/user/{email}")
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

    @PutMapping("/user/role")
    public Map<String, Object> updateRole(@Valid @RequestBody RoleBody roleBody, @AuthenticationPrincipal User admin) {
        User user = userService.loadByUsername(roleBody.getUser());
        if (user.hasRole(User.Role.ADMINISTRATOR) != roleBody.getRole().equals(User.Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot combine ADMINISTRATOR with other roles");
        } else {
            if (user.addRole(roleBody.getRole())) {
                userService.updateUser(user);
                secLogger.info(LoggerConfig.getEventLogMap(admin.getUsername(), "Grant role %s to %s".formatted(roleBody.getRole(), roleBody.getUser()),
                        "GRANT_ROLE", "/api/admin/user/role"));
            }
            return user.getUserMap();
        }
    }

    @DeleteMapping("/user/role")
    public Map<String, Object> deleteRole(@Valid @RequestBody RoleBody roleBody, @AuthenticationPrincipal User admin) {
        User user = userService.loadByUsername(roleBody.getUser());
        if (!user.hasRole(roleBody.getRole())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User role not found");
        } else {
            if (roleBody.getRole().equals(User.Role.ADMINISTRATOR)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove ADMINISTRATOR");
            } else if (user.getRoles().size() == 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove user's only role");
            }
            user.getRoles().remove(roleBody.getRole());
            userService.updateUser(user);
            secLogger.info(LoggerConfig.getEventLogMap(admin.getUsername(), "Remove role %s from %s".formatted(roleBody.getRole(), roleBody.getUser()),
                    "REMOVE_ROLE", "/api/admin/user/role"));
            return user.getUserMap();
        }
    }
}

@Data
@NoArgsConstructor
class RoleBody {
    @NotEmpty
    private String user;
    private User.Role role;
}

@Data
@NoArgsConstructor
class LockBody {
    @NotEmpty
    private String user;
    @Pattern(regexp = "LOCK|UNLOCK")
    private String operation;
}