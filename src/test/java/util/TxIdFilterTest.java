package util;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.util.TxIdFilter;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class TxIdFilterTest {

    @Test
    void usesIncomingHeaderAndCleansMdcAfter() throws IOException, ServletException {
        TxIdFilter filter = new TxIdFilter();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(req.getHeader(TxIdFilter.HEADER)).thenReturn("abc-123");

        AtomicBoolean chainSawMdc = new AtomicBoolean(false);

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) {
                // Inside the chain, MDC must be populated
                assertEquals("abc-123", MDC.get(TxIdFilter.MDC_KEY));
                chainSawMdc.set(true);
            }
        };

        // Precondition: MDC empty
        assertNull(MDC.get(TxIdFilter.MDC_KEY));

        filter.doFilter(req, res, chain);

        // After filter returns, MDC must be cleared
        assertNull(MDC.get(TxIdFilter.MDC_KEY));
        assertTrue(chainSawMdc.get(), "Expected chain to be invoked");

        // Response should echo the same header
        verify(res).setHeader(TxIdFilter.HEADER, "abc-123");
    }

    @Test
    void generatesUuidWhenHeaderMissing() throws IOException, ServletException {
        TxIdFilter filter = new TxIdFilter();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(req.getHeader(TxIdFilter.HEADER)).thenReturn(null);

        AtomicReference<String> valueSeenInChain = new AtomicReference<>();

        FilterChain chain = (request, response) -> valueSeenInChain.set(MDC.get(TxIdFilter.MDC_KEY));

        filter.doFilter(req, res, chain);

        String mdcValue = valueSeenInChain.get();
        assertNotNull(mdcValue);
        assertFalse(mdcValue.isBlank());

        // Must be a valid UUID
        assertDoesNotThrow(() -> UUID.fromString(mdcValue));

        // Response header should match what was in MDC
        verify(res).setHeader(TxIdFilter.HEADER, mdcValue);

        // After invocation MDC is cleared
        assertNull(MDC.get(TxIdFilter.MDC_KEY));
    }

    @Test
    void treatsBlankHeaderAsMissing() throws IOException, ServletException {
        TxIdFilter filter = new TxIdFilter();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        when(req.getHeader(TxIdFilter.HEADER)).thenReturn("   "); // blank

        AtomicReference<String> valueSeenInChain = new AtomicReference<>();

        FilterChain chain = (request, response) -> valueSeenInChain.set(MDC.get(TxIdFilter.MDC_KEY));

        filter.doFilter(req, res, chain);

        String mdcValue = valueSeenInChain.get();
        assertNotNull(mdcValue);
        assertFalse(mdcValue.isBlank());
        assertDoesNotThrow(() -> UUID.fromString(mdcValue));

        verify(res).setHeader(TxIdFilter.HEADER, mdcValue);
        assertNull(MDC.get(TxIdFilter.MDC_KEY));
    }
}
