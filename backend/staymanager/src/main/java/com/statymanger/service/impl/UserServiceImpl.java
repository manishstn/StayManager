package com.statymanger.service.impl;


import com.statymanger.dto.LoginRequestDTO;
import com.statymanger.dto.Response;
import com.statymanger.dto.UserDTO;
import com.statymanger.entity.RevokedToken;
import com.statymanger.entity.User;
import com.statymanger.exception.UserNameNotFoundException;
import com.statymanger.repository.RevokedTokenRepository;
import com.statymanger.repository.UserRepository;
import com.statymanger.service.UserService;
import com.statymanger.utils.JWTUtils;
import com.statymanger.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service

@RequiredArgsConstructor

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RevokedTokenRepository revokedTokenRepository;

    @Override
    public Response register(User user) {
        Response response = new Response();
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Username is already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        UserDTO userDTO = Utils.mapUserEntityToUserDTO(savedUser);
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setUser(userDTO);
        return response;
    }

    @Override
    public Response login(LoginRequestDTO loginRequestDTO) {

        Response response = new Response();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password()));
        var user = userRepository.findByEmail(loginRequestDTO.email()).orElseThrow(() -> new UserNameNotFoundException("user Not found"));
        var token = jwtUtils.generateToken(user);
        response.setStatusCode(HttpStatus.OK.value());
        response.setToken(token);
        response.setRole(user.getRole());
        response.setExpirationTime(String.valueOf(jwtUtils.getExpirationTimeInMillis(token)));
        return response;
    }

    @Override
    public Response getUserInfo(String email) {
        Response response = new Response();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNameNotFoundException("user Not found"));
        UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
        response.setStatusCode(HttpStatus.OK.value());
        response.setUser(userDTO);
        response.setMessage("Success");
        return response;
    }

    @Override
    public Response getAllUsers() {
        Response response = new Response();
        List<User> userList = userRepository.findAll();
        List<UserDTO> userDTOList = Utils.mapUserListEntityToUserListDTO(userList);
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setUserList(userDTOList);
        return response;
    }

    @Override
    public Response getUserBookingHistory(String userId) {
        Response response = new Response();
        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new UserNameNotFoundException("User Not Found"));
        UserDTO userDTO = Utils.mapUserEntityToUserDTOPlusBookings(user);
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setUser(userDTO);
        return response;
    }

    @Override
    public Response deleteUser(String userId) {
        Response response = new Response();
        userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new UserNameNotFoundException("User Not Found"));
        userRepository.deleteById(Long.valueOf(userId));
        response.setStatusCode(200);
        response.setMessage("successful");
        return response;
    }

    @Override
    public Response getUserById(String userId) {
        Response response = new Response();

        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new UserNameNotFoundException("User Not Found"));
        UserDTO userDTO = Utils.mapUserEntityToUserDTO(user);
        response.setStatusCode(200);
        response.setMessage("successful");
        response.setUser(userDTO);
        return response;
    }

    @Override
    public Response logout(String authHeader) {
        Response response = new Response();
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Get expiry from JWT (assuming your jwtUtils returns Date or Long)
            long expiryMillis = jwtUtils.getExpirationTimeInMillis(token);
            LocalDateTime expiryDateTime = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(expiryMillis),
                    java.time.ZoneId.systemDefault()
            );

            // Save to DB
            RevokedToken revokedToken = RevokedToken.builder()
                    .token(token)
                    .expiryDate(expiryDateTime)
                    .build();

            revokedTokenRepository.save(revokedToken);

            response.setStatusCode(200);
            response.setMessage("Logged out successfully");
        }
        return response;
    }
}