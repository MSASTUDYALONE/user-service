package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import org.apache.catalina.User;

public interface UserService {
    UserDto createUser(UserDto userDto);
}
