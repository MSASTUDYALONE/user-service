package com.example.userservice.valueObject;

import lombok.Data;

@Data
public class ResponseUser {
    private String email;
    private String name;
    private String userId;
}
