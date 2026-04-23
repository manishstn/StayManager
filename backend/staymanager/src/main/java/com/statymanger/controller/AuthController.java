package com.statymanger.controller;

import com.statymanger.dto.LoginRequestDTO;
import com.statymanger.dto.Response;
import com.statymanger.entity.User;
import com.statymanger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@RequestBody User user) {
        Response response = userService.register(user);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Response response = userService.login(loginRequestDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


}