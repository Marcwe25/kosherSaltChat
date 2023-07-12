package com.mw.security.auth;

import com.mw.security.token.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationService service;
  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.authenticate(request));
  }
  @PostMapping("/refresh-token")
  public void refreshToken(@RequestBody Token token, HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      service.refreshToken(token,request, response);
    } catch (Exception e) {
        response.sendError(HttpStatus.NOT_ACCEPTABLE.value());
    }
    }
  }