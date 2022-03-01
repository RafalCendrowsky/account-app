package com.rafalcendrowski.AccountApplication.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServicelTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService = new UserServiceImpl();

    @Test
    void loadByUsername() {
        assertThrows(IllegalArgumentException.class, () -> userService.loadByUsername("test"));
    }

    @Test
    void saveUser() {
        when(userRepository.save(any(User.class))).then(returnsFirstArg());
        User user = new User();
        assertEquals(userService.saveUser(user), user);
    }

    @Test
    void deleteUser() {
        User user = new User();
        userService.deleteUser(user);
        verify(userRepository).delete(user);
    }

    @Test
    void loadAllUsers() {
    }

    @Test
    void hasUser() {
    }
}