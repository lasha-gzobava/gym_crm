package controller;

import org.example.controller.AuthController;
import org.example.dto.login.PasswordChangeDto;
import org.example.entity.User;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test: Successful login
    @Test
    void login_shouldReturnSuccessResponse_whenCredentialsAreValid() {
        User user = new User("John", "Doe", "john.doe", "encodedPass");
        when(userService.authenticate("john.doe", "password")).thenReturn(user);

        ResponseEntity<String> response = authController.login("john.doe", "password");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Login successful for john.doe", response.getBody());
    }

    // ❌ Test: Invalid login
    @Test
    void login_shouldReturnUnauthorized_whenCredentialsAreInvalid() {
        when(userService.authenticate("john.doe", "wrong")).thenThrow(new RuntimeException("Invalid credentials"));

        ResponseEntity<String> response = authController.login("john.doe", "wrong");

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid username or password", response.getBody());
    }

    // ✅ Test: Successful password change
    @Test
    void changePassword_shouldReturnSuccessResponse_whenValid() {
        PasswordChangeDto dto = new PasswordChangeDto("john.doe", "oldPass", "newPass");

        doNothing().when(userService).changePassword("john.doe", "oldPass", "newPass");

        ResponseEntity<String> response = authController.changePassword(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Password changed successfully", response.getBody());
    }

    // ❌ Test: Failed password change (e.g., old password incorrect)
    @Test
    void changePassword_shouldReturnUnauthorized_whenChangeFails() {
        PasswordChangeDto dto = new PasswordChangeDto("john.doe", "wrongOld", "newPass");

        doThrow(new RuntimeException("Old password does not match."))
                .when(userService).changePassword(dto.getUsername(), dto.getOldPassword(), dto.getNewPassword());

        ResponseEntity<String> response = authController.changePassword(dto);

        assertEquals(401, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Password change failed"));
    }
}
