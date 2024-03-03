package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User selectById(int id);
    User selectByEmail(String email);
    User selectByName(String username);
    int insertUser(User user);
    int updateStatus(int id,int status);
    int updateHeader(int id,String headerUrl);
    int updatePassword(int id,String password);

}
