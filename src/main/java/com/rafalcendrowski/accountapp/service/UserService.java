package com.rafalcendrowski.accountapp.service;

import com.rafalcendrowski.accountapp.api.user.request.RegisterUserRequest;
import com.rafalcendrowski.accountapp.api.user.request.UserRoleRequest;
import com.rafalcendrowski.accountapp.api.user.response.UserResponse;
import com.rafalcendrowski.accountapp.exceptions.EntityNotFoundException;
import com.rafalcendrowski.accountapp.mapper.UserMapper;
import com.rafalcendrowski.accountapp.model.user.User;
import com.rafalcendrowski.accountapp.model.user.UserRepository;
import com.rafalcendrowski.accountapp.model.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final Set<UserRole> DEFAULT_ROLES = Set.of(UserRole.USER);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> loadAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserResponse getById(String id) {
        var user = getEntityById(id);
        return userMapper.toResponse(user);
    }

    public UserResponse getByUsername(String username) {
        var user = getEntityByUsername(username);
        return userMapper.toResponse(user);
    }

    private User getEntityById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private User getEntityByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public UserResponse registerUser(RegisterUserRequest request) {
        var user = userMapper.toModel(request);
        user.setRoles(DEFAULT_ROLES);
        try {
            userRepository.save(user);
        } catch (DuplicateKeyException e) {
            throw new EntityNotFoundException("User already exists");
        }
        return userMapper.toResponse(user);
    }

    public UserResponse updateUserRoles(String id, UserRoleRequest request) {
        var user = getEntityById(id);
        var roles = new HashSet<>(request.roles());
        roles.addAll(DEFAULT_ROLES);
        user.setRoles(roles);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public void delete(String id) {
        var user = getEntityById(id);
        userRepository.delete(user);
    }
}