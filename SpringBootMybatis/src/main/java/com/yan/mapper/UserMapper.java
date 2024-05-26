package com.yan.mapper;

import com.yan.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User getUser();
}
