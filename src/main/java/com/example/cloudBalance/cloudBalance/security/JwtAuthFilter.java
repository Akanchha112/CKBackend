package com.example.cloudBalance.cloudBalance.security;

import com.example.cloudBalance.cloudBalance.model.User;
import com.example.cloudBalance.cloudBalance.repository.UserRepository;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String tokenHeader=request.getHeader("Authorization");
        if(tokenHeader==null || !tokenHeader.startsWith("Bearer")){
            filterChain.doFilter(request,response );
            return;
        }

        String token=tokenHeader.split(" ")[1];
        String email=authUtils.getEmailFromToken(token);

        if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            User user=userRepository.findByEmailId(email).orElseThrow();
            UsernamePasswordAuthenticationToken token1=new UsernamePasswordAuthenticationToken(user, null, List.of()
            );

            SecurityContextHolder.getContext().setAuthentication(token1);
        }
        filterChain.doFilter(request,response);
    }
}
