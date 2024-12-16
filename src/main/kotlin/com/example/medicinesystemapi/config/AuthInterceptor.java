package com.example.medicinesystemapi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//            return false;
//        }
//        String token = authHeader.substring(7);
//        // Здесь можно добавить логику проверки токена, например, с использованием JWT
//        // Если токен не валиден, вернуть false
//        if (token.isEmpty()){
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token is null");
//            return false;
//        }
//        return true;
//    }
}