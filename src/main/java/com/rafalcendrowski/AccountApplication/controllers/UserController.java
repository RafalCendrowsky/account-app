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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
                .map(userModelAssembler::toModel).toList();
        return CollectionModel.of(userList,
                linkTo(methodOn(UserController.class).getUsers()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<UserDto> getUser(@PathVariable Long id) {
        User user = userService.loadById(id);
        return userModelAssembler.toModel(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @AuthenticationPrincipal User admin) {
        User user = userService.loadById(id);
        if (user.hasRole(User.Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR!");
        } else {
            userService.deleteUser(user);
            secLogger.info(LoggerConfig.getEventLogMap(admin.getUsername(), user.getUsername(),
                    "DELETE_USER", "/api/admin/user"));
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/{id}/role")
    public EntityModel<UserDto> updateRole(@PathVariable Long id, @Valid @RequestBody User.Role role, @AuthenticationPrincipal User admin) {
        User user = userService.loadById(id);
        if (user.hasRole(User.Role.ADMINISTRATOR) != role.equals(User.Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot combine ADMINISTRATOR with other roles");
        } else {
            if (user.addRole(role)) {
                userService.updateUser(user);
                secLogger.info(LoggerConfig.getEventLogMap(admin.getUsername(), "Grant role %s to %s".formatted(role, user.getUsername()),
                        "GRANT_ROLE", "/api/admin/user/role"));
            }
            return userModelAssembler.toModel(user);
        }
    }

    @DeleteMapping("/{id}/role")
    public EntityModel<UserDto> deleteRole(@PathVariable Long id, @Valid @RequestBody User.Role role, @AuthenticationPrincipal User admin) {
        User user = userService.loadById(id);
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
            secLogger.info(LoggerConfig.getEventLogMap(admin.getUsername(), "Remove role %s from %s".formatted(role, user.getUsername()),
                    "REMOVE_ROLE", "/api/admin/user/role"));
            return userModelAssembler.toModel(user);
        }
    }
}