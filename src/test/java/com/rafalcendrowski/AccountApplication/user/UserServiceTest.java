package com.rafalcendrowski.AccountApplication.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService = new UserServiceImpl();

    @Test
    void loadByUsername() {
        try {
            userService.loadByUsername("test");
        } catch (IllegalArgumentException ignored) {}
        verify(userRepository).findByUsername("test");
    }

    @Test
    void loadByUsername_invalid_args() {
        assertThrows(IllegalArgumentException.class, () -> userService.loadByUsername("test"));
    }

    @Test
    void saveUser() {
        when(userRepository.save(any(User.class))).then(returnsFirstArg());
        User user = new User();
        assertEquals(userService.updateUser(user), user);
    }

    @Test
    void deleteUser() {
        User user = new User();
        userService.deleteUser(user);
        verify(userRepository).delete(user);
    }

    @Test
    void loadAllUsers() {
        User user = new User();
        when(userRepository.findAll()).thenReturn(List.of(user));
        assertEquals(userService.loadAllUsers(), List.of(user));
    }

    @Test
    void hasUser() {
        User user = new User();
        when(userRepository.findByUsername("test")).thenReturn(user);
        assertTrue(userService.hasUser("test"));
        assertFalse(userService.hasUser("not test"));
    }
}