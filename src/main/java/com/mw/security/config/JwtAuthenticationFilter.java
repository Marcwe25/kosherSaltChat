package com.mw.security.config;

import com.mw.security.token.Token;
import com.mw.security.token.TokenIssuer;
import com.mw.security.token.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final TokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain)
          throws ServletException, IOException {

    // not filtering autjentication request
    logger.warn("--- USING MY FILTER ---");
    String path = request.getServletPath();
    if (request.getServletPath().contains("/api/v1/auth")) {
      logger.warn("not authenticating ::: request is an autentication request :",path);
      filterChain.doFilter(request, response);
      return;
    }

    // starting jwt reading
    String accessToken = null;
    String userEmail = null;

    // for jsock request jwt is in queries
    if (request.getServletPath().contains("chat-room-websocket")) {
        String[] queries = request.getQueryString().split("&");
        for (String query : queries){
          if(query.startsWith("accesstoken")){
            accessToken = "Bearer " + query.split("=")[1]; }
        }
    }

    // in other case jwt is read from headers
    if (accessToken==null){
      accessToken = request.getHeader("Authorization");
    }

    // rejecting if no token have been found
    if (accessToken == null ||!accessToken.startsWith("Bearer")) {
      logger.warn("not authenticating ::: no authorisation found");
      filterChain.doFilter(request, response);
      return;
    }

    final String f_jwt = accessToken;
//            .replace("^Bearer\\s+","");
    logger.warn("f_jwt is : " + f_jwt);
    // rejecting if jwt is not from the app
    if( !jwtService.isTokenIssuedByKChat(f_jwt)){
      logger.warn("not authenticating ::: external authentication");
      final HttpServletRequestWrapper reqWrapper = new HttpServletRequestWrapper(request) {
        @Override
        public String getHeader(String name) {
          if ("Authorization".equals(name)) {
            return f_jwt;
          }
          return super.getHeader(name);
        }
      };
      filterChain.doFilter(reqWrapper, response);
      return;
    }

  // extracting user email
    final String jwt = f_jwt.replace("Bearer ","");
    logger.warn("jwt is : " + jwt);

    userEmail = jwtService.extractUsername(jwt);
    if(userEmail == null || userEmail.length() == 0){
      logger.warn("no email in jwt");
      filterChain.doFilter(request, response);
      return;
    }

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      // getting user details
      UserDetails userDetails;
      try {
        userDetails = this.userDetailsService.loadUserByUsername(userEmail);
      } catch (UsernameNotFoundException e) {
        logger.warn("not authenticating ::: user email not registered");
        throw e;
      }

      // checking token validity in repository
      Optional<Token> optionalToken = tokenRepository.findByToken(jwt);
      Boolean isTokenValid = optionalToken
          .map(t -> !t.isExpired() && !t.isRevoked())
          .orElse(false);

      if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        final HttpServletRequestWrapper reqWrapper = new HttpServletRequestWrapper(request) {
          @Override
          public String getHeader(String name) {
            if ("Authorization".equals(name)) {
              return null;
            }
            return super.getHeader(name);
          }
        };
        filterChain.doFilter(reqWrapper, response);
        return;

      }
    }
    filterChain.doFilter(request, response);
  }


}
