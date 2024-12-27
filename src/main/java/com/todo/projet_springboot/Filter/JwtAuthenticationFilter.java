package com.todo.projet_springboot.Filter;

import com.todo.projet_springboot.Config.JwtTokenUtil;
import com.todo.projet_springboot.Entity.RefreshToken;
import com.todo.projet_springboot.Service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private RefreshTokenService refreshTokenService;

    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = (String) request.getSession().getAttribute("token");

        if (accessToken != null && jwtTokenUtil.validateToken(accessToken)) {
            String username = jwtTokenUtil.getUserEmailFromToken(accessToken);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else {
            // Check if a valid refresh token exists in the session
            String refreshToken = (String) request.getSession().getAttribute("refreshToken");
            if (refreshToken != null) {
                try {
                    // Validate refresh token
                    RefreshToken validRefreshToken = refreshTokenService.validateRefreshToken(refreshToken);
                    String newAccessToken = jwtTokenUtil.generateToken(validRefreshToken.getUser());

                    // Store the new access token in the session
                    request.getSession().setAttribute("token", newAccessToken);

                    // Authenticate the user
                    String username = validRefreshToken.getUser().getEmail();
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (Exception e) {
                    // Refresh token is invalid, do nothing
                }
            }
        }

        filterChain.doFilter(request, response);
    }

}
