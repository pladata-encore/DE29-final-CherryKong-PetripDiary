package com.example.finalproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.finalproject.config.handler.LoginAuthFailureHandler;
import com.example.finalproject.config.handler.LoginAuthSuccessHandler;
import com.example.finalproject.config.handler.LogoutAuthSuccesshandler;

@Configuration
@EnableWebSecurity // 여러가지 설정 중에서 시큐리티 설정
// @Secured 어노테이션 활성화, @PreAuthorize 어노테이션 활성화  
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) 
public class SecurityConfig {
    // 비밀번호 암호화에서 사용할 객체
    @Bean
    public BCryptPasswordEncoder eCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private LoginAuthSuccessHandler loginAuthSuccessHandler;
    @Autowired
    private LoginAuthFailureHandler loginAuthFailureHandler;
    @Autowired
    private LogoutAuthSuccesshandler logoutAuthSuccesshandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/checkLoginStatus").permitAll() // 이 매처를 가장 먼저 추가
                .requestMatchers("/user/**").authenticated()
                .requestMatchers("/manager/**").hasAnyAuthority("MANAGER", "ADMIN")
                .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")
                .anyRequest().permitAll()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/loginPage")
                .loginProcessingUrl("/login")
                .successHandler(loginAuthSuccessHandler)
                .failureHandler(loginAuthFailureHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutAuthSuccesshandler)
                .permitAll()
            );

        return http.build();
    }
}