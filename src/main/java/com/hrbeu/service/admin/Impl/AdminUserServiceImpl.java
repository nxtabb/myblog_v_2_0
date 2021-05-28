package com.hrbeu.service.admin.Impl;

import com.hrbeu.dao.admin.AdminUserDao;
import com.hrbeu.pojo.User;
import com.hrbeu.service.admin.AdminUserService;
import com.hrbeu.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname AdminUserServiceImpl
 * @Description TODO
 * @Date 2021/5/9 20:31
 * @Created by nxt
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {
    @Autowired
    private AdminUserDao adminUserDao;
    @Override
    public User checkUser(String username, String password) {
        User user = adminUserDao.queryUser(username);
        if(user==null){
            return null;
        }else {
            if(user.getPassword().equals(MD5Util.md5(password))){
                return user;
            }else {
                return null;
            }
        }
    }
}
