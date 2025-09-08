package health;


import org.example.health.DatabaseQuickHealth;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class DatabaseQuickHealthTest {

    @Test
    void health_isUp_whenSelect1Succeeds() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForObject("SELECT 1", Integer.class)).thenReturn(1);

        DatabaseQuickHealth health = new DatabaseQuickHealth(jdbc);

        Health result = health.health();

        assertEquals(Health.up().withDetail("select1", 1).build().getStatus(), result.getStatus());
        assertEquals(1, result.getDetails().get("select1"));
        verify(jdbc).queryForObject("SELECT 1", Integer.class);
    }

    @Test
    void health_isDown_whenQueryThrows() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForObject("SELECT 1", Integer.class)).thenThrow(new RuntimeException("boom"));

        DatabaseQuickHealth health = new DatabaseQuickHealth(jdbc);

        Health result = health.health();

        assertEquals("DOWN", result.getStatus().getCode());
        assertFalse(result.getDetails().containsKey("select1"));
        verify(jdbc).queryForObject("SELECT 1", Integer.class);
    }
}
