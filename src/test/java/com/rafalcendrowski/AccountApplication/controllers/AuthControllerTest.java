package com.rafalcendrowski.AccountApplication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafalcendrowski.AccountApplication.models.UserModelAssembler;
import com.rafalcendrowski.AccountApplication.user.UserRegisterDto;
import com.rafalcendrowski.AccountApplication.user.UserService;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;
    @MockBean
    UserDetailsService detailsService;
    @MockBean
    UserModelAssembler userModelAssembler;
    @MockBean
    AccessDeniedHandler accessDeniedHandler;
    @MockBean
    PasswordEncoder encoder;
    @MockBean
    Logger logger;

    private static UserRegisterDto testUser;

    @BeforeAll
    static void setUpUser() {
        testUser = new UserRegisterDto("test name", "test lastname",
                "test@acme.com", "testvalidpassword");
    }

    @Test
    void testWithValidInputReturnsOK() throws Exception {
        when(encoder.encode(any(String.class))).thenReturn("encoded password");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource(value = "invalidUserBodySource")
    void testWithInvalidUserBody(UserRegisterDto user) throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithUserAlreadyExists() throws Exception {
        when(userService.hasUser(any(String.class))).thenReturn(true);
        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithBreachedPassword() throws  Exception {
        UserRegisterDto user = new UserRegisterDto("name", "lastname",
                "email@acme.com", "breachedPassword");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    public static Stream<Arguments> invalidUserBodySource() {
        return Stream.of(
                Arguments.arguments(new UserRegisterDto("", "lastname",
                        "email@acme.com", "validpassword")),
                Arguments.arguments(new UserRegisterDto("name", "",
                        "email@acme.com", "validpassword")),
                Arguments.arguments(new UserRegisterDto("name", "lastname",
                        "", "validpassword")),
                Arguments.arguments(new UserRegisterDto("name", "lastname",
                        "email@acme.com", "")),
                Arguments.arguments(new UserRegisterDto("name", "lastname",
                        "email@invalid.com", "validpassword")),
                Arguments.arguments(new UserRegisterDto("name", "lastname",
                        "email@acme.com", "tooshort"))
        );
    }
}