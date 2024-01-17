package com.example.userservice.valueObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//import lombok.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
//@AllArgsConstructor
//@NoArgsConstructor
public class Greeting {
    @Value("${greeting.message}")
    private String message;
}
