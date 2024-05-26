package com.yan.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Integer uid;

    private String userName;

    private String password;

    private LocalDateTime createTime;
}
