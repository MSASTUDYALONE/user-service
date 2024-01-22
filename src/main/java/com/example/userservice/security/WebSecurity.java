package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.Filter;

@Configuration // 다른 빈들보다 먼저  추가
@EnableWebSecurity // WebSecurity 용도이다
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    private Environment env;

    public WebSecurity(BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService, Environment env) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.env = env;
    }

    //    # 권한 Configure
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); // csrf 사용하지 않겠다.
//        http.authorizeRequests().antMatchers("/users/**").permitAll(); // authorize Requests허용할 수 있는 작업은 "/users/**"에 Match되면 PermitAll해라
        http.authorizeRequests().antMatchers("/**") // 모든 요청에 대해
//                .permitAll()
                .hasIpAddress("localhost")// 해당 IP만 받을 것이다.
                .and() // 그리고
                .addFilter(getAuthentication()); // 통과된 데이터에 한해서만(해당 IP만) 필터적용하겠다.

        http.headers().frameOptions().disable(); // 헤더의 프레임옵션을 사용하지 않겠다
    }

    private AuthenticationFilter getAuthentication() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager()); // Spring Security에서 가져온 매니저를 가지고 인증처리 하겠다.
        return authenticationFilter;
    }

//   # 인증 Configure
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder); // userService: 사용자 세부 정보를 얻어오도록 설정
    }
}
