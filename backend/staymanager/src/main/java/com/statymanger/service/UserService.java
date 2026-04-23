package com.statymanger.service;

import com.statymanger.dto.LoginRequestDTO;
import com.statymanger.dto.Response;
import com.statymanger.entity.User;

public interface UserService {
    Response register(User user);

    Response login(LoginRequestDTO loginRequestDTO);

    Response getUserInfo(String email);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);
}