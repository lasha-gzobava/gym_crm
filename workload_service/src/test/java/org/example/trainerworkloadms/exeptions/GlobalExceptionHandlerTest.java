package org.example.trainerworkloadms.exeptions;

import jakarta.persistence.EntityNotFoundException;
import org.example.trainerworkloadms.expetions.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandlerTest.DummyController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GlobalExceptionHandler exceptionHandler;

    @RestController
    static class DummyController {
        @GetMapping("/test/status")
        public void throwStatus() {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request occurred");
        }

        @GetMapping("/test/status-null")
        public void throwStatusNull() {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null);
        }

        @GetMapping("/test/notfound")
        public void throwNotFound() {
            throw new EntityNotFoundException("Trainer not found");
        }

        @GetMapping("/test/notfound-null")
        public void throwNotFoundNull() {
            throw new EntityNotFoundException((Exception) null);
        }

        @GetMapping("/test/generic")
        public void throwGeneric() {
            throw new RuntimeException("Unexpected error");
        }

        @GetMapping("/test/validation")
        public void throwValidation() throws MethodArgumentNotValidException, NoSuchMethodException {
            BindingResult mockResult = mock(BindingResult.class);
            when(mockResult.getFieldErrors())
                    .thenReturn(List.of(new FieldError("obj", "username", "must not be blank")));

            // use a real MethodParameter (not null)
            var method = DummyController.class.getDeclaredMethod("throwValidation");
            var param = new org.springframework.core.MethodParameter(method, -1);

            throw new MethodArgumentNotValidException(param, mockResult);
        }

    }

    // ResponseStatusException with reason
    @Test
    void handleResponseStatusException() throws Exception {
        mockMvc.perform(get("/test/status").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Bad request occurred"))
                .andExpect(jsonPath("$.status").value(400));
    }

    // ResponseStatusException with null reason (edge case)
    @Test
    void handleResponseStatusException_NullReason() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, null);
        var response = exceptionHandler.handleResponseStatusException(ex);
        assert response.getStatusCode().value() == 400;
        assert response.getBody().get("status").equals(400);
    }

    // EntityNotFoundException with a message
    @Test
    void handleEntityNotFoundException() throws Exception {
        mockMvc.perform(get("/test/notfound").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.message").value("Trainer not found"));
    }


    // EntityNotFoundException with a null message (edge case)
    @Test
    void handleEntityNotFound_NullMessage() {
        EntityNotFoundException ex = new EntityNotFoundException();
        var response = exceptionHandler.handleEntityNotFound(ex);
        assert response.getStatusCode().value() == 404;
        assert response.getBody().get("status").equals(404);
    }


   // Generic Exception Test

    @Test
    void handleGenericException() throws Exception {
        mockMvc.perform(get("/test/generic").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred. Please contact support."));
    }


   // Validation Error Test

    @Test
    void handleValidationException() throws Exception {
        mockMvc.perform(get("/test/validation").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details[0]").value("username: must not be blank"));
    }


    // Direct unit test for generic Exception
    @Test
    void handleGenericDirect() {
        Exception ex = new RuntimeException("Something exploded");
        var response = exceptionHandler.handleGeneric(ex);
        assert response.getStatusCode().value() == 500;
        assert response.getBody().get("error").equals("Internal Server Error");
    }
}
