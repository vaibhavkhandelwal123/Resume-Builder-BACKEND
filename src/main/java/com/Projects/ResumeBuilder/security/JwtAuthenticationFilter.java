package com.Projects.ResumeBuilder.security;

import com.Projects.ResumeBuilder.Entity.User;
import com.Projects.ResumeBuilder.Repository.UserRepository;
import com.Projects.ResumeBuilder.Utilities.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token= null;
        String userId = null;
        if(authHeader!=null && authHeader.startsWith("Bearer")){
            token = authHeader.substring(7);
            try{
                userId = jwtUtil.getUserIdFromToken(token);
            }catch (Exception e){
                log.error("Token is not valid/available");
            }
        }
        if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null){
            try{
                if(jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)){
                    User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not Found"));
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user,null,new ArrayList<>());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
               log.error("Exception occurred while validating the token");
            }
        }
        filterChain.doFilter(request,response);
    }
}
