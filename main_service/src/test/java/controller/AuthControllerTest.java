package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.AuthController;
import org.example.dto.login.PasswordChangeDto;
import org.example.util.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.example.security.JwtService;
import org.example.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper om;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        var controller = new AuthController(authenticationManager, jwtService, userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        om = new ObjectMapper();
    }

    // ---------- /auth/login ----------



    @Test
    void login_returns400_whenValidationFails() throws Exception {
        // missing password
        var body = """
            {"username":"john.doe"}
        """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("password")));
    }

    // ---------- /auth/password ----------

    @Test
    void changePassword_returns200_whenValid() throws Exception {
        var dto = new PasswordChangeDto("john.doe", "oldPass", "newPass");
        doNothing().when(userService).changePassword(dto.getUsername(), dto.getOldPassword(), dto.getNewPassword());

        mockMvc.perform(put("/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));
    }

    @Test
    void changePassword_returns400_whenOldPasswordWrong() throws Exception {
        var dto = new PasswordChangeDto("john.doe", "wrongOld", "newPass");

        doThrow(new IllegalArgumentException("Old password does not match."))
                .when(userService)
                .changePassword(dto.getUsername(), dto.getOldPassword(), dto.getNewPassword());

        mockMvc.perform(put("/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Old password does not match")));
    }

    @Test
    void changePassword_returns400_whenValidationFails() throws Exception {
        // missing username
        var invalid = """
            {"oldPassword":"a","newPassword":"b"}
        """;

        mockMvc.perform(put("/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("username")));
    }
}
