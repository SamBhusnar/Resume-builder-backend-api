package com.reer.resumebuilder.resume_builder_api.security;


import com.reer.resumebuilder.resume_builder_api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtility;
    private final CustomUserDetailsService userDetailService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("inside dofilter method");
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith(("Bearer "))) {
            log.info("token is present");
            token = authHeader.substring(7);
            // validate token
            username = jwtUtility.extractUsername(token);

            // set it coming object in security context


        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("token is valid");

            UserDetails userDetails = userDetailService.loadUserByUsername(username);
            if (jwtUtility.validateToken(username, userDetails, token)) {
                log.info("token is valid inside if");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("token is valid inside if end");
            }

        }
        filterChain.doFilter(request, response);

    }
}
