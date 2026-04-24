package com.statymanger.controller;

import com.statymanger.dto.LoginRequestDTO;
import com.statymanger.dto.Response;
import com.statymanger.entity.User;
import com.statymanger.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for User Registration, Login, and Logout")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account in the system.")
    public ResponseEntity<Response> register(@RequestBody User user) {
        Response response = userService.register(user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user and returns a JWT token.")
    public ResponseEntity<Response> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Response response = userService.login(loginRequestDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "User Logout", description = "Invalidates the current JWT token by adding it to the blacklist.")
    public ResponseEntity<Response> logout(@RequestHeader("Authorization") String authHeader) {
        Response response = userService.logout(authHeader);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}