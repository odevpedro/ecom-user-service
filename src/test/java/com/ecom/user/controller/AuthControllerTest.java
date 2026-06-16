package com.ecom.user.controller;

import com.ecom.user.dto.CreateUserRequest;
import com.ecom.user.dto.UserResponse;
import com.ecom.user.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerReturns201() throws Exception {
        var request = new CreateUserRequest();
        request.setName("Joao");
        request.setEmail("joao@email.com");
        request.setPassword("123456");

        var response = UserResponse.fromEntity(new com.ecom.user.model.User());
        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/users/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerWithInvalidEmailReturns400() throws Exception {
        var request = new CreateUserRequest();
        request.setName("Joao");
        request.setEmail("invalid");
        request.setPassword("123456");

        mockMvc.perform(post("/api/users/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
