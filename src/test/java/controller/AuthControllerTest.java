package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.AuthController;
import org.example.dto.login.PasswordChangeDto;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.util.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper om;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthController authController = new AuthController(userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        om = new ObjectMapper();
    }

    //  Test: Successful login
    @Test
    void login_shouldReturnSuccessResponse_whenCredentialsAreValid() {
        User user = new User("John", "Doe", "john.doe", "encodedPass");
        when(userService.authenticate("john.doe", "password")).thenReturn(user);

        ResponseEntity<String> response = authController.login("john.doe", "password");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Login successful", response.getBody());
    }

    //  Test: Invalid login
    @Test
    void login_shouldThrow_whenCredentialsAreInvalid() {
        when(userService.authenticate("john.doe", "wrong"))
                .thenThrow(new RuntimeException("Invalid username or password"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authController.login("john.doe", "wrong"));

        assertEquals("Invalid username or password", ex.getMessage());
    }


    //  Test: Successful password change
    @Test
    void changePassword_shouldReturnSuccessResponse_whenValid() {
        PasswordChangeDto dto = new PasswordChangeDto("john.doe", "oldPass", "newPass");

        doNothing().when(userService).changePassword("john.doe", "oldPass", "newPass");

        ResponseEntity<String> response = authController.changePassword(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Password changed successfully", response.getBody());
    }

    //  Test: Failed password change (e.g., old password incorrect)
    @Test
    void changePassword_shouldThrow_whenChangeFails_unitStyle() {
        PasswordChangeDto dto = new PasswordChangeDto("john.doe", "wrongOld", "newPass");
        doThrow(new IllegalArgumentException("Old password does not match."))
                .when(userService)
                .changePassword(dto.getUsername(), dto.getOldPassword(), dto.getNewPassword());

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, () -> authController.changePassword(dto));
        assertTrue(ex.getMessage().contains("Old password does not match."));
    }
}
