package com.harsha.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FormLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        String email = authentication.getName();

        // Extract role for embedding in JWT
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String role = isAdmin ? "ADMIN" : "USER";

        // Generate JWT with email + role
        String token = jwtUtil.generateToken(email, role);

        // Store JWT in HttpOnly cookie (not accessible by JS — prevents XSS theft)
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);       // JS cannot read this cookie
        cookie.setSecure(false);        // set true in production (HTTPS only)
        cookie.setPath("/");
        cookie.setMaxAge(86400);        // 1 day in seconds
        response.addCookie(cookie);

        // Redirect based on role
        response.sendRedirect(isAdmin ? "/admin/dashboard" : "/index");
    }
}