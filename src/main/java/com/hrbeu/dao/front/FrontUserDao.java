package com.hrbeu.dao.front;

import com.hrbeu.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Classname FrontUserDao
 * @Description TODO
 * @Date 2021/5/14 10:07
 * @Created by nxt
 */
@Repository
public interface FrontUserDao {
    void userRegister(@Param("user") User user);
    Integer queryUserNameCount(@Param("username") String username);
    User checkUser(@Param("username") String username,@Param("password") String password);
}
