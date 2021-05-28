package com.hrbeu.service.admin;

import com.hrbeu.pojo.User;

/**
 * @Classname AdminUserService
 * @Description TODO
 * @Date 2021/5/9 20:31
 * @Created by nxt
 */
public interface AdminUserService {
    User checkUser(String username,String password);
}
