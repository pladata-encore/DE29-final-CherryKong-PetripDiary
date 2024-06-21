package com.example.finalproject.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginStatusController {

    @GetMapping("/checkLoginStatus")
    public Map<String, Boolean> checkLoginStatus(Authentication authentication) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("isLogin", authentication != null && authentication.isAuthenticated());
        return response;
    }
}