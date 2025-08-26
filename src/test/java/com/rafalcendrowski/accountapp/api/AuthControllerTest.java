package com.rafalcendrowski.accountapp.api;

import com.rafalcendrowski.accountapp.api.auth.AuthController;
import com.rafalcendrowski.accountapp.api.auth.request.LoginRequest;
import com.rafalcendrowski.accountapp.model.user.User;
import com.rafalcendrowski.accountapp.security.JwtComponent;
import com.rafalcendrowski.accountapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = AuthController.class)
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    private final AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtComponent jwtComponent;

    AuthControllerTest(AuthController authController) {
        this.authController = authController;
    }

    @Test
    void login_success_returnsToken() {
        // Arrange
        var request = new LoginRequest("testUser", "testPassword");
        var userDetails = createTestUserDetails();

        when(userService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(passwordEncoder.matches("testPassword", "testEncodedPassword")).thenReturn(true);
        when(jwtComponent.generateToken(userDetails)).thenReturn("jwtToken");

        // Act
        var response = authController.login(request);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(Map.of("token", "jwtToken"), response.getBody());
    }

    @Test
    void login_invalidPassword_returnsUnauthorized() {
        // Arrange
        LoginRequest request = new LoginRequest("testUser", "wrongPassword");
        UserDetails userDetails = createTestUserDetails();

        when(userService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(passwordEncoder.encode("wrongPassword")).thenReturn("wrongEncoded");

        // Act
        var response = authController.login(request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void login_userNotFound_throwsException() {
        // Arrange
        LoginRequest request = new LoginRequest("unknownUser", "password");

        when(userService.loadUserByUsername("unknownUser"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authController.login(request));
    }

    private UserDetails createTestUserDetails() {
        var user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedTestPassword");
        return user;
    }
}