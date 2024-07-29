package com.ecommerce.webapp.config;


import com.ecommerce.webapp.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String token = request.getHeader("Authorization");
        if(token != null){
            String username = jwtUtil.getSubject(token);
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails user = userDetailsService.loadUserByUsername(username);
                boolean isValid = jwtUtil.isValidToken(token, user.getUsername());
                if(isValid){
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken( username, user.getPassword(), user.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }


            }

        }

        filterChain.doFilter(request, response);

    }

}
