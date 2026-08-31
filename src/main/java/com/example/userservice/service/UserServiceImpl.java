package com.example.userservice.service;

import com.example.userservice.dto.req.UserInformationForUserService;
import com.example.userservice.entity.User;
import com.example.userservice.exception.ApplicationErrors;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER - SERVICE")
public class UserServiceImpl implements UserService{
    private final UserRepository userRepo;
    private final UserMapper userMapper;
    @Override
    public User create(UserInformationForUserService information) {
    if(information.getUserId() == null){
        throw  ApplicationErrors.INVALID_USER_STATUS;
    }
    User user = userMapper.fromUserInformationForUserService(information);
        return userRepo.save(user);
    }

    @Override
    public User getUserById(String userId) {
        User user =  userRepo.findById(userId).orElseThrow(() -> ApplicationErrors.USER_NOT_FOUND);
        return user;
    }

    }
}
