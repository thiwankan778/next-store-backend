package com.TutorTrack.demo.filter;

import com.TutorTrack.demo.entity.Token;
import com.TutorTrack.demo.repos.TokenRepository;
import com.TutorTrack.demo.services.JwtService;
import com.TutorTrack.demo.services.UserInfoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserInfoService userDetailsService;

    @Autowired
    TokenRepository tokenRepository;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            email = jwtService.extractUsername(token);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try{

                String tokenType = jwtService.extractClaim(token, claims -> claims.get("tokenType", String.class));
                if (!"accessToken".equals(tokenType)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token type");
                    return;
                }

            }catch(Exception e){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            Optional<Token> tokenOptional=tokenRepository.findByAccessToken(token);



            if (tokenOptional.isPresent() && jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }else{
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token or token not found");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

}
