package com.yan.exchange;

import com.yan.common.ResponseBody;
import com.yan.domain.User;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * 用户服务
 */
@HttpExchange(url = "http://localhost:8080/user", accept = "application/json", contentType = "application/json")
public interface UserExchange {

    /**
     * 调用外部服务查询用户信息
     *
     * @param id 用户id
     * @return 用户信息
     */
    @GetExchange("{id}")
    ResponseBody<User> getUser(@PathVariable("id") Integer id);
}
