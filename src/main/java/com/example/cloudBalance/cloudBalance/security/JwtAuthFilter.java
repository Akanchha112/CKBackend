package com.example.cloudBalance.cloudBalance.security;

import com.example.cloudBalance.cloudBalance.model.RefreshToken;
import com.example.cloudBalance.cloudBalance.model.User;
import com.example.cloudBalance.cloudBalance.repository.UserRepository;
import com.example.cloudBalance.cloudBalance.service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final AuthUtils authUtils;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String tokenHeader=request.getHeader("Authorization");
        String refreshHeader = request.getHeader("X-Refresh-Token");
        if(tokenHeader==null || !tokenHeader.startsWith("Bearer")){
            filterChain.doFilter(request,response );
            return;
        }

        String token=tokenHeader.split(" ")[1];

        try {

            String email = authUtils.getEmailFromToken(token);
            String role = authUtils.getRoleFromToken(token);

            List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (refreshHeader != null) {
                refreshTokenService.validateAndUpdateActivity(refreshHeader);
            }

        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("ACCESS_TOKEN_EXPIRED");
            return;
        }

        filterChain.doFilter(request, response);
    }


}
