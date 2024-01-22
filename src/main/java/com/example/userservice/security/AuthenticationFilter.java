package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private UserService userService;
    private Environment env; // 왜 필요? 토큰에 대한 만료기간, 토큰을 만들기 위해서 특정한 키워드를 넣어서 그 키를 이용해서 알고리즘을 사용할 때 .yml에 넣는다.

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    //    # 로그인을 시도할 때
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            return getAuthenticationManager().authenticate( // 로그인한 creds값과 변경된 token값을 비교해주겠다.
                    new UsernamePasswordAuthenticationToken(creds.getEmail(),creds.getPassword(), new ArrayList<>())
                    // 입력한 정보를 Token으로 변경, new ArrayList<>(): 어떤 권한을 줄 것인지
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//     # 로그인 성공했을 때 (ex.값반환, 토큰만료시간 등등)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        super.successfulAuthentication(request, response, chain, authResult);
//        log.debug(((User)authResult.getPrincipal()).getUsername());

        // import org.springframework.security.core.userdetails.User;
        String userName = ((User)authResult.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(userName);
    }
}