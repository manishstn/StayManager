package com.statymanger.service.impl;


import com.statymanger.dto.LoginRequestDTO;
import com.statymanger.dto.Response;
import com.statymanger.dto.UserDTO;
import com.statymanger.entity.User;
import com.statymanger.exception.UserNameNotFoundException;
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


@Service

@RequiredArgsConstructor

public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWTUtils jwtUtils;

    private final AuthenticationManager authenticationManager;


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

}