package com.microtakeout.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.microtakeout.common.constant.UserRole;
import com.microtakeout.common.exception.NotFoundException;
import com.microtakeout.common.exception.UnauthorizedException;
import com.microtakeout.common.exception.ValidationException;
import com.microtakeout.common.util.JwtUtil;
import com.microtakeout.user.dto.LoginRequest;
import com.microtakeout.user.dto.LoginResponse;
import com.microtakeout.user.dto.RegisterRequest;
import com.microtakeout.user.entity.User;
import com.microtakeout.user.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * 用户服务
 */
@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public User register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, request.username());
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new ValidationException("用户名已存在");
        }

        // 检查邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, request.email());
        if (userMapper.selectCount(emailWrapper) > 0) {
            throw new ValidationException("邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setRole(request.role());
        user.setEmailVerified(false);
        user.setActive(true);

        userMapper.insert(user);

        // 发送邮箱验证事件
        String verificationToken = UUID.randomUUID().toString();
        kafkaTemplate.send("user-events", Map.of(
            "eventType", "USER_REGISTERED",
            "userId", user.getId(),
            "email", user.getEmail(),
            "verificationToken", verificationToken
        ));

        log.info("用户注册成功: {}", user.getUsername());
        return user;
    }

    public LoginResponse login(LoginRequest request) {
        // 查找用户（通过用户名或邮箱）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(User::getUsername, request.usernameOrEmail())
            .or().eq(User::getEmail, request.usernameOrEmail()));
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new UnauthorizedException("用户名或密码错误");
        }

        if (!user.getActive()) {
            throw new UnauthorizedException("账户已被禁用");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("用户名或密码错误");
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        log.info("用户登录成功: {}", user.getUsername());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new NotFoundException("用户", id);
        }
        return user;
    }

    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new NotFoundException("用户", username);
        }
        return user;
    }

    @Transactional
    public void verifyEmail(Long userId, String token) {
        User user = getUserById(userId);
        // 这里应该验证token，简化处理
        user.setEmailVerified(true);
        userMapper.updateById(user);
        log.info("邮箱验证成功: userId={}", userId);
    }

    @Transactional
    public void resetPassword(String email, String newPassword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new NotFoundException("用户", email);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        log.info("密码重置成功: email={}", email);
    }
}

