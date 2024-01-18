package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.valueObject.Greeting;
import com.example.userservice.valueObject.RequestUser;
import com.example.userservice.valueObject.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class UserController {
    private Environment env;
    private UserService userService;
    private Greeting greeting;  // # @Value

    @Autowired
    public UserController(Environment env, UserService userService, Greeting greeting) {
        this.env = env;
        this.userService = userService;
        this.greeting = greeting;
    }



    @GetMapping("/user-service/health_check")
    public String status() {
        return String.format("It's Working in User Service on Port %s", env.getProperty("local.server.port")); // port번호 가져오기
    }

//    @GetMapping("/health_check")
//    public String status(HttpServletRequest request) {
//        return String.format("It's Working in User Service on Port %s", request.getServerPort()); // port번호 가져오기
//    }


    @GetMapping("/user-service/welcome")
    public String welcome() {
        return env.getProperty("greeting.message");
//        return greeting.getMessage();
    }



    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }




}
