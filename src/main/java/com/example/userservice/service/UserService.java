package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import org.apache.catalina.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService { // UserDetailsService : 이거 security때문에 받음
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll(); // Iterable: 반복적인 데이터

    UserDto getUserDetailsByEmail(String userName);
}
