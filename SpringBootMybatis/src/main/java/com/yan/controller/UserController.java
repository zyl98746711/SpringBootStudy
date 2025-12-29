package com.yan.controller;

import com.yan.common.ResponseBody;
import com.yan.domain.User;
import com.yan.exchange.UserExchange;
import com.yan.logging.AppLogger;
import com.yan.mapper.UserMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import lombok.RequiredArgsConstructor;

import static java.lang.StringTemplate.STR;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final UserExchange userExchange;


    @GetMapping("{id}")
    public ResponseBody<User> getUser(@PathVariable("id") Integer id) {
        String message = STR."查询用户id:\{id}信息";
        AppLogger.log(message, id);
        User user = userMapper.getUser(id);
        if (Objects.isNull(user)) {
            return ResponseBody.success(null);
        }
        return ResponseBody.success(user);
    }

    @GetMapping("/remote/{id}")
    public ResponseBody<User> getRemoteUser(@PathVariable("id") Integer id) {
        AppLogger.log("查询用户id:{}信息", id);
        return userExchange.getUser(id);
    }
}
