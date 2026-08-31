package com.example.userservice.service;

import com.example.userservice.dto.req.UserInformationForUserService;
import com.example.userservice.entity.User;
import jakarta.validation.Valid;

public interface UserService {
    User create(@Valid UserInformationForUserService information);

    User getUserById(String userId);


}
