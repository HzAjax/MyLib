package ru.volodin.logging_lib.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.volodin.logging_lib.config.LoggingProperties;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoggingFilter implements Filter {

    private final LoggingProperties properties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!properties.isHttpFilterEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

            String method = wrappedRequest.getMethod();
            String uri = wrappedRequest.getRequestURI();
            String query = wrappedRequest.getQueryString();

            if (!"OFF".equalsIgnoreCase(properties.getLevel())) {
                log.info("Incoming request: {} {}{}", method, uri, query != null ? "?" + query : "");
            }

            chain.doFilter(wrappedRequest, wrappedResponse);

            String requestBody = new String(wrappedRequest.getContentAsByteArray(), wrappedRequest.getCharacterEncoding());
            String responseBody = new String(wrappedResponse.getContentAsByteArray(), wrappedResponse.getCharacterEncoding());

            int status = wrappedResponse.getStatus();
            if (!"OFF".equalsIgnoreCase(properties.getLevel())) {
                log.info("Outgoing response: {} {} -> HTTP {}", method, uri, status);
            }

            if ("DEBUG".equalsIgnoreCase(properties.getLevel())) {
                log.debug("Request body: {}", requestBody);
                log.debug("Response body: {}", responseBody);
            }

            wrappedResponse.copyBodyToResponse();
        } else {
            chain.doFilter(request, response);
        }
    }
}
