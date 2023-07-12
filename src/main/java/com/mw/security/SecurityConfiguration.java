package com.mw.security;

import com.mw.security.auth.AuthenticationEntryPointJwt;
import com.mw.security.config.JwtAuthenticationFilter;
import com.mw.security.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.mw.security.user.Permission.ADMIN_CREATE;
import static com.mw.security.user.Permission.ADMIN_DELETE;
import static com.mw.security.user.Permission.ADMIN_READ;
import static com.mw.security.user.Permission.ADMIN_UPDATE;
import static com.mw.security.user.Permission.MANAGER_CREATE;
import static com.mw.security.user.Permission.MANAGER_DELETE;
import static com.mw.security.user.Permission.MANAGER_READ;
import static com.mw.security.user.Permission.MANAGER_UPDATE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final LogoutHandler logoutHandler;

  @Bean
  public AuthenticationEntryPoint authenticationEntryPointJwt(){
    return new AuthenticationEntryPointJwt();
  }
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .cors()
            .and()
            .csrf()
            .disable()
            .authorizeHttpRequests()
            .requestMatchers(
                "/api/v1/auth/**",
                "/v2/api-docs",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui/**",
                "/webjars/**",
                "/swagger-ui.html"
            )
            .permitAll()
            .anyRequest()
            .authenticated()
//            .and()
//            .sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPointJwt())
            .and()
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout()
            .logoutUrl("/api/v1/auth/logout")
            .addLogoutHandler(logoutHandler)
            .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
            .and()
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
    ;
    return http.build();
  }

//  @Bean
//  public SecurityFilterChain oAuth2FilterChain(HttpSecurity http) throws Exception {
//    http
//            .cors()
//            .and()
//            .csrf()
//            .disable()
//            .authorizeHttpRequests()
//            .requestMatchers(
//                    "/api/v1/auth/**",
//                    "/v2/api-docs",
//                    "/v3/api-docs",
//                    "/v3/api-docs/**",
//                    "/swagger-resources",
//                    "/swagger-resources/**",
//                    "/configuration/ui",
//                    "/configuration/security",
//                    "/swagger-ui/**",
//                    "/webjars/**",
//                    "/swagger-ui.html"
//                    ,
//                    "chat-room-websocket"
//            )
//            .permitAll()
//            .anyRequest()
//            .authenticated()
//            .and()
//            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
//            ;
//    return http.build();
//  }
}
