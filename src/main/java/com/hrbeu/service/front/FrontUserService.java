package com.hrbeu.service.front;

import com.hrbeu.pojo.User;

/**
 * @Classname FrontUserService
 * @Description TODO
 * @Date 2021/5/14 09:58
 * @Created by nxt
 */
public interface FrontUserService {
    void userRegister(User user);
    Integer queryUserNameCount(String username);
    User checkUser(String username, String password);
}
