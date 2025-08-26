package com.rafalcendrowski.accountapp.user;

import com.rafalcendrowski.accountapp.exceptions.EntityNotFoundException;
import com.rafalcendrowski.accountapp.model.user.User;
import com.rafalcendrowski.accountapp.model.user.UserRepository;
import com.rafalcendrowski.accountapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService = new UserService();

    @Test
    void loadByUsername() {
        try {
            userService.loadByUsername("test");
        } catch (EntityNotFoundException ignored) {
        }
        verify(userRepository).findByUsername("test");
    }

    @Test
    void loadByUsername_invalid_args() {
        assertThrows(EntityNotFoundException.class, () -> userService.loadByUsername("test"));
    }

    @Test
    void registerUser() {
        when(userRepository.save(any(User.class))).then(returnsFirstArg());
        User user = new User();
        assertEquals(userService.registerUser(user), user);
    }

    @Test
    void updateUser() {
        when(userRepository.save(any(User.class))).then(returnsFirstArg());
        when(userRepository.findByUsername(any(String.class))).thenReturn(new User());
        User user = new User();
        user.setUsername("test");
        assertEquals(userService.updateUser(user), user);
    }

    @Test
    void updateInvalidUser() {
        User user = new User();
        user.setUsername("test");
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(user));
    }

    @Test
    void deleteUser() {
        User user = new User();
        userService.delete(user);
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