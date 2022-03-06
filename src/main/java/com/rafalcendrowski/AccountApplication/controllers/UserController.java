package com.rafalcendrowski.AccountApplication.controllers;

import com.rafalcendrowski.AccountApplication.models.UserModelAssembler;
import com.rafalcendrowski.AccountApplication.user.User;
import com.rafalcendrowski.AccountApplication.logging.LoggerConfig;
import com.rafalcendrowski.AccountApplication.user.UserDto;
import com.rafalcendrowski.AccountApplication.user.UserService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    Logger secLogger;

    @Autowired
    UserModelAssembler userModelAssembler;

    @GetMapping
    public CollectionModel<EntityModel<UserDto>> getUsers() {
        List<EntityModel<UserDto>> userList = userService.loadAllUsers().stream()
                .map(userModelAssembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(userList);
    }

    @GetMapping("/{email}")
    public EntityModel<UserDto> getUser(@PathVariable String email) {
        User user = userService.loadByUsername(email);
        return userModelAssembler.toModel(user);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email, @AuthenticationPrincipal User admin) {
        User user = userService.loadByUsername(email);
        if (user.hasRole(User.Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR!");
        } else {
            userService.deleteUser(user);
            secLogger.info(LoggerConfig.getEventLogMap(admin.getUsername(), email,
                    "DELETE_USER", "/api/admin/user"));
            return ResponseEntity.noContent().build();
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