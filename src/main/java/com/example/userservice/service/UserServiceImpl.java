package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService{
    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;
    Environment env;
    RestTemplate restTemplate;
    OrderServiceClient orderServiceClient;
    CircuitBreakerFactory circuitBreakerFactory;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, Environment env, RestTemplate restTemplate, OrderServiceClient orderServiceClient, CircuitBreakerFactory circuitBreakerFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.restTemplate = restTemplate;
        this.orderServiceClient = orderServiceClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); // 매칭전략을 강력하게(딱 맞아떨어지지 않으면 지정할수 없도록) 설정
        UserEntity userEntity = mapper.map(userDto, UserEntity.class); // UserEntity로 변환
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd())); // 값이 아직 구현이 안됐기 때문에 기본값을 넣어두겠다.

        userRepository.save(userEntity);


        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);

        return returnUserDto;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findAllByUserId(userId);

        if (userEntity == null)
            throw new UsernameNotFoundException("User not found");

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class); // (바꾸고 싶은 변수, 바꾸고 싶은 "클래스")

//        List<ResponseOrder> orders = new ArrayList<>();

        /* Using as rest template */
//        String orderUrl = "http://127.0.0.1:800/order-service/%s/orders"; // user-serivce.yml로 이동
//        String orderUrl = String.format(env.getProperty("order_service.url"), userId); // %s에 userId를 넣어주기 위해 String.format 사용
//        ResponseEntity<List<ResponseOrder>> orderListResponse = restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//                new ParameterizedTypeReference<List<ResponseOrder>>() {
//        });
//        List<ResponseOrder> orderList = orderListResponse.getBody();


        /* Using a feign client */
        /* Feign exception handling */
//        List<ResponseOrder> orderList = orderServiceClient.getOrder(userId);
//        List<ResponseOrder> orderList = null;
//        try {
//            orderList = orderServiceClient.getOrder(userId);
//        } catch (FeignException ex) {
//            log.error(ex.getMessage());
//        }

        /* ErrorDecoder */
//        List<ResponseOrder> orderList = orderServiceClient.getOrder(userId);

//        circuitbreaker 추가 -> 원래는 order service가 오류 나면 바로 에러떳는데 200보여주고 빈 리스트 보여주게 처리한다

        log.info("Before call orders microservice");
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker"); // circuitbreaker 생성
        List<ResponseOrder> orderList = circuitbreaker.run(() -> orderServiceClient.getOrder(userId), throwable -> new ArrayList<>()); // circuitbreaker 실행
        // run(() -> 정상작동할 경우 반환값, 문제 생겼을 경우 반환값)
        log.info("After call orders microservice");
        userDto.setOrders(orderList);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username); // username이 email임..

        if (userEntity == null)
            throw new UsernameNotFoundException(username);

        // return : 로그인이 모두 통과되었을 때 진행, new ArrayList<>() : 권한리스트
        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(), true, true, true, true, new ArrayList<>());
        //security user 객체
        //모두 검색이 잘 되었다면 해당 유저를 반환하겠다
        //마지막은 권한값
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        return userDto;
    }
}

