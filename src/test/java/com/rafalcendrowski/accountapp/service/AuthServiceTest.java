package com.rafalcendrowski.accountapp.service;

import com.rafalcendrowski.accountapp.api.auth.request.LoginRequest;
import com.rafalcendrowski.accountapp.exceptions.LoginException;
import com.rafalcendrowski.accountapp.persistance.user.User;
import com.rafalcendrowski.accountapp.persistance.user.UserRole;
import com.rafalcendrowski.accountapp.properties.SecurityProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    final static String SECRET = "secret";
    final static long EXPIRATION_TIME = 3600; // 1 hour in

    @Mock
    UserService userService;

    @Mock
    PasswordEncoder passwordEncoder;

    final AuthService authService;

    AuthServiceTest() {
        this.authService = new AuthService(
                userService,
                new SecurityProperties(new SecurityProperties.Jwt(SECRET, EXPIRATION_TIME)),
                passwordEncoder
        );
    }

    @Test
    void generateToken_validUserDetails_returnsToken() {
        // Arrange
        var userDetails = createTestUserDetails();

        // Act
        var token = authService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_validUserDetails_tokenContainsCorrectClaims() {
        // Arrange
        var userDetails = createTestUserDetails();

        // Act
        var token = authService.generateToken(userDetails);
        var decodedToken = authService.verifyToken(token);

        // Assert
        assertNotNull(decodedToken);
        assertEquals(decodedToken.getSubject(), userDetails.getUsername());
        assertArrayEquals(
                decodedToken.getClaims().get("roles").asArray(String.class),
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toArray(String[]::new)
        );
    }


    @Test
    void login_success_returnsToken() {
        // Arrange
        var request = new LoginRequest("testUser", "testPassword");
        var userDetails = createTestUserDetails();

        when(userService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(passwordEncoder.matches("testPassword", "testEncodedPassword")).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> authService.authenticate(request));
    }

    @Test
    void login_invalidPassword_throwsException() {
        // Arrange
        var request = new LoginRequest("testUser", "wrongPassword");
        var userDetails = createTestUserDetails();

        when(userService.loadUserByUsername("testUser")).thenReturn(userDetails);
        when(passwordEncoder.encode("wrongPassword")).thenReturn("wrongEncoded");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> authService.authenticate(request));
        assertEquals(LoginException.class, exception.getClass());
    }

    @Test
    void login_userNotFound_throwsException() {
        // Arrange
        var request = new LoginRequest("unknownUser", "password");

        when(userService.loadUserByUsername("unknownUser")).thenThrow(new UsernameNotFoundException("User not found"));

        // Act & Assert
        var exception = assertThrows(Exception.class, () -> authService.authenticate(request));
        assertEquals(LoginException.class, exception.getClass());
    }

    private UserDetails createTestUserDetails() {
        var user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedTestPassword");
        user.setRoles(Set.of(UserRole.USER, UserRole.ADMINISTRATOR));
        return user;
    }
}