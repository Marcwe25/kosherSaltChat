package com.mw.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;


public class AuthenticationEntryPointJwt implements AuthenticationEntryPoint {



        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                AuthenticationException authException) throws IOException {
            response.setHeader("mimim","LALALA");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setHeader("mimim",authException.getCause().getMessage());
//            throw new CustomAuthenticationException(message,authException.getCause());
        }

}
//            if(authException instanceof CustomSecurityException) {
//                CustomSecurityException customSecurityException = (CustomSecurityException) authException;
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setHeader("security-message",customSecurityException.customMessage);
//                System.out.println("ddddd1");
//            } else {
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                response.setHeader("security-message",authException.getMessage());
//                System.out.println("ddddd2");
//
//            }


//    @Override
//    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException authenticationException) throws IOException {
//
//        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//        final Map<String, Object> body = new HashMap<>();
//        body.put("code", HttpServletResponse.SC_UNAUTHORIZED);
//        body.put("payload", "You need to login first in order to perform this action.");
//
//        final ObjectMapper mapper = new ObjectMapper();
//        mapper.writeValue(httpServletResponse.getOutputStream(), body);
//    }}

//            logger.error("Unauthorized error: {}", authException.getMessage());
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
