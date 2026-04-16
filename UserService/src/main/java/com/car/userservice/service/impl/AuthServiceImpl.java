package com.car.userservice.service.impl;

import com.car.userservice.dto.LoginRequest;
import com.car.userservice.dto.LoginResponse;
import com.car.userservice.entity.User;
import com.car.userservice.exception.DomainException;
import com.car.userservice.jwt.JwtTokenService;
import com.car.userservice.repository.UserRepository;
import com.car.userservice.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private static final String FIXED_PASSWORD = "123456";

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl(UserRepository userRepository, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        logger.info("登录API被调用");
        if (!FIXED_PASSWORD.equals(request.getPassword())) {
            throw new DomainException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "用户名或密码错误");
        }
        String loginName = request.getLoginName().trim();
        User user = userRepository.findByLoginName(loginName)
                .orElseThrow(() -> new DomainException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "用户名或密码错误"));
        String token = jwtTokenService.createToken(user.getId(), user.getLoginName());
        return LoginResponse.bearer(token);
    }
}
