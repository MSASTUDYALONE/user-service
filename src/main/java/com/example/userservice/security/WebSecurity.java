package com.example.userservice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration // 다른 빈들보다 먼저  추가
@EnableWebSecurity // WebSecurity 용도이다
public class WebSecurity extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); // csrf 사용하지 않겠다.
        http.authorizeRequests().antMatchers("/users/**").permitAll(); // authorize Requests허용할 수 있는 작업은 "/users/**"에 Match되면 PermitAll해라

        http.headers().frameOptions().disable(); // 헤더의 프레임옵션을 사용하지 않겠다
    }
}
