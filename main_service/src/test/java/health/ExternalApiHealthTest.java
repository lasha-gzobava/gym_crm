package health;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.example.health.ExternalApiHealth;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ExternalApiHealthTest {

    private static MockWebServer server;

    @BeforeAll
    static void start() throws IOException {
        server = new MockWebServer();
        server.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                String path = request.getPath();
                if ("/ok".equals(path)) {
                    return new MockResponse().setResponseCode(200);
                }
                if ("/fail".equals(path)) {
                    return new MockResponse().setResponseCode(503);
                }
                return new MockResponse().setResponseCode(404);
            }
        });
        server.start();
    }

    @AfterAll
    static void stop() throws IOException {
        if (server != null) server.shutdown();
    }

    @Test
    void health_isUp_on2xx() {
        String url = server.url("/ok").toString();
        ExternalApiHealth health = new ExternalApiHealth(url);

        Health result = health.health();

        assertEquals("UP", result.getStatus().getCode());
        assertEquals(url, result.getDetails().get("url"));
        if (result.getDetails().containsKey("status")) {
            assertEquals(200, result.getDetails().get("status"));
        }
    }

    @Test
    void health_isDown_onNon2xx() {
        String url = server.url("/fail").toString();
        ExternalApiHealth health = new ExternalApiHealth(url);

        Health result = health.health();

        assertEquals("DOWN", result.getStatus().getCode());
        assertEquals(url, result.getDetails().get("url"));
        if (result.getDetails().containsKey("status")) {
            assertEquals(503, result.getDetails().get("status"));
        }
    }

    @Test
    void health_isDown_onConnectionError() {
        // Call a definitely closed port to force ConnectException, regardless of timeouts.
        String url = "http://127.0.0.1:1/boom";
        ExternalApiHealth health = new ExternalApiHealth(url);

        Health result = health.health();

        assertEquals("DOWN", result.getStatus().getCode());
        assertEquals(url, result.getDetails().get("url"));
        assertTrue(result.getDetails().containsKey("error"), "Expected 'error' detail on failure");
        assertTrue(result.getDetails().containsKey("message"), "Expected 'message' detail on failure");
    }
}
