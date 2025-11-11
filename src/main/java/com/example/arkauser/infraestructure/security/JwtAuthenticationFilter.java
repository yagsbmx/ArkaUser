package com.example.arkauser.infraestructure.security;

import com.example.arkauser.application.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AntPathMatcher matcher = new AntPathMatcher();

    private static final List<String> PUBLIC_PATHS = List.of(
            "/error",
            "/auth/**",
            "/users/login",
            "/users/create",
            "/api/users/login",
            "/api/users/create",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/**"
    );

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String path = request.getServletPath();
        for (String p : PUBLIC_PATHS) {
            if (matcher.match(p, path)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7).trim();
        if (token.isEmpty() || !jwtService.isValidToken(token)) {
            chain.doFilter(request, response);
            return;
        }

        String username = jwtService.getSubject(token);
        if (username == null || username.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        try {
            var userDetails = userDetailsService.loadUserByUsername(username);
            var authorities = userDetails.getAuthorities();
            Long userId = jwtService.getUserId(token);
            var principal = UserPrincipal.builder()
                    .id(userId)
                    .username(userDetails.getUsername())
                    .password("")
                    .authorities(authorities)
                    .enabled(userDetails.isEnabled())
                    .build();

            var authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    token,
                    authorities
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UsernameNotFoundException ex) {
        }

        chain.doFilter(request, response);
    }
}
