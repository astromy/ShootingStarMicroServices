package com.astromyllc.astroorb.config;

import com.astromyllc.astroorb.utils.TokenHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TokenCaptureFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // Wrap request to ensure we can read headers multiple times
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        try {
            // Extract token from Authorization header
            String authHeader = wrappedRequest.getHeader("Authorization");
            String token = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                TokenHolder.setToken(token);
                //  System.out.println("✅ Token captured for request: " + wrappedRequest.getRequestURI());
            } else {
                // System.out.println("ℹ️ No Bearer token in request: " + wrappedRequest.getRequestURI());
            }

            // Continue the filter chain
            chain.doFilter(wrappedRequest, response);

        } finally {
            // Always clear the token after request completes
            TokenHolder.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip filtering for public endpoints if needed
        String path = request.getRequestURI();
        return path.startsWith("/public") || path.startsWith("/webhook") || path.startsWith("/resources");
    }
}
