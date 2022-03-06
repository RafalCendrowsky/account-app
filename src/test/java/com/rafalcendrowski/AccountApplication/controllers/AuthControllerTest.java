package com.rafalcendrowski.AccountApplication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafalcendrowski.AccountApplication.user.UserService;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
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
    AccessDeniedHandler accessDeniedHandler;
    @MockBean
    PasswordEncoder encoder;
    @MockBean
    Logger logger;

    @Test
    void testWithValidInputReturnsOK() throws Exception {
        when(encoder.encode(any(String.class))).thenReturn("encoded password");
        UserBody user = new UserBody("test name", "test lastname",
                "test@acme.com", "testvalidpassword");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }
}