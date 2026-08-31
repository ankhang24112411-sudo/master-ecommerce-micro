package com.example.userservice.mapper;

import com.example.userservice.dto.req.UserInformationForUserService;
import com.example.userservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User fromUserInformationForUserService(UserInformationForUserService userInformationForUserService);
}
