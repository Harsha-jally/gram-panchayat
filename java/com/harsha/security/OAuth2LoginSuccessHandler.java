package com.harsha.security;

import com.harsha.entity.User;
import com.harsha.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        System.out.println("OAuth Data: " + oAuth2User.getAttributes());

        String email = (String) oAuth2User.getAttributes().get("email");
        String name  = (String) oAuth2User.getAttributes().get("name");

        if (email == null) {
            throw new RuntimeException("Email not received from Google");
        }

        // Save user if not already present
        User user = userService.getUserByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            u.setName(name);
            u.setPassword("OAUTH_USER");
            u.setRole("ROLE_USER");
            User saved = userService.saveUser(u);
            System.out.println("New OAuth user saved: " + saved.getEmail());
            return saved;
        });

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // Set JWT in HttpOnly cookie
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 day
        cookie.setSecure(false);        // Set to true in production (HTTPS)
        response.addCookie(cookie);

        // Clear any saved authentication attributes (prevents redirect back to /login)
        clearAuthenticationAttributes(request);

        // Redirect using strategy — respects context path correctly
        getRedirectStrategy().sendRedirect(request, response,
                request.getContextPath() + "/index");
    }
}