package com.rafalcendrowski.AccountApplication;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin")
public class AdminController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/user")
    public List<Map<String, Object>> getUsers() {
        List<Map<String, Object>> userList = new ArrayList<>();
        for (User user: userRepository.findAll()) {
            userList.add(user.getUserMap());
        }
        return userList;
    }

    @DeleteMapping("/user/{email}")
    public Map<String, String> deleteUser(@PathVariable String email) {
        User user = userRepository.findByUsername(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        } else if (user.getRoles().contains(User.Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR!");
        } else {
            userRepository.delete(user);
            return Map.of("status", "Deleted successfully", "user", email);
        }
    }

    @PutMapping("/user/role")
    public Map<String, Object> updateRole(@Valid @RequestBody RoleBody roleBody) {
        User user = userRepository.findByUsername(roleBody.getUser());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        } else if (user.getRoles().contains(User.Role.ADMINISTRATOR) || roleBody.getRole().equals(User.Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify ADMINISTRATOR");
        } else {
            user.getRoles().add(roleBody.getRole());
            userRepository.save(user);
            return user.getUserMap();
        }
    }

    @DeleteMapping("/user/role")
    public Map<String, Object> deleteRole(@Valid @RequestBody RoleBody roleBody) {
        User user = userRepository.findByUsername(roleBody.getUser());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        } else if (user.getRoles().contains(User.Role.ADMINISTRATOR) || roleBody.getRole().equals(User.Role.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot modify ADMINISTRATOR");
        } else if(user.getRoles().contains(roleBody.getRole()) && user.getRoles().size() == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot remove user's only role");
        } else {
            if (user.getRoles().contains(roleBody.getRole())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User role not found");
            } else {
                user.getRoles().remove(roleBody.getRole());
                userRepository.save(user);
                return user.getUserMap();
            }
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