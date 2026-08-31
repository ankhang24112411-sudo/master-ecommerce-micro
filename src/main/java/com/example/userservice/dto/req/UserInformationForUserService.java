package com.example.userservice.dto.req;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class UserInformationForUserService {

    private String userId;

    private String email;

    private String username;

    private String address;

    private String phone;

    private String fullName;
}

