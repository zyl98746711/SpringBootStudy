package com.yan.controller;

import com.common.ResponseBody;
import com.logging.AppLogger;
import com.yan.domain.User;
import com.yan.mapper.UserMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("user")
public class UserController {
    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @GetMapping("{id}")
    public ResponseBody<User> getUser(@PathVariable("id") Integer id) {
        AppLogger.log("查询用户id:{}信息", id);
        User user = userMapper.getUser();
        if (Objects.isNull(user)) {
            return ResponseBody.success(null);
        }
        return ResponseBody.success(user);
    }
}
