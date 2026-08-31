package com.example.userservice.controller;


import com.example.userservice.dto.BaseResponse;
import com.example.userservice.dto.req.UserInformationForUserService;
import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PostMapping("/create")
    public ResponseEntity<BaseResponse<User>> create(
            @RequestBody
            @Valid
            UserInformationForUserService information
    ) {

        User user = userService.create(information);
        BaseResponse<User> response = new BaseResponse<>();
        response.setData(user);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/me")
    public ResponseEntity<BaseResponse<User>> getMyInfo(JwtAuthenticationToken token) {
        String userId = token.getToken().getSubject();
        return ResponseEntity.ok(new BaseResponse<>(userService.getUserById(userId), "Get user information successfully"));
    }

}