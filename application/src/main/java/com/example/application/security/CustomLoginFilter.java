package com.example.application.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomLoginFilter extends BasicAuthenticationFilter {

    private final ObjectMapper mapper = new ObjectMapper();
    private JwtService jwtService;

    public CustomLoginFilter(AuthenticationManager authenticationManager, JwtService jwtService) {
        super(authenticationManager);
        this.jwtService = jwtService;
    }

    @Override
    protected void onSuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication auth
    ) throws IOException {
        String token = jwtService.generateToken(auth); // здесь должна быть генерация с передачей auth
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("status","success");
        props.put("token", token);

        response.getWriter().write(mapper.writeValueAsString(props));
        response.getWriter().flush();
        response.getWriter().close();
    }

    @Override
    protected void onUnsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failure
    )throws IOException{

        response.setContentType("application/json");
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("status","failure");
        props.put("message", failure.getMessage());
        response.getWriter().write(mapper.writeValueAsString(props));
    }
}
