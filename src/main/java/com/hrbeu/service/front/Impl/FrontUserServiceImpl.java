package com.hrbeu.service.front.Impl;

import com.hrbeu.dao.front.FrontUserDao;
import com.hrbeu.pojo.User;
import com.hrbeu.service.front.FrontUserService;
import com.hrbeu.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Classname FrontUserServiceImpl
 * @Description TODO
 * @Date 2021/5/14 10:05
 * @Created by nxt
 */
@Service
public class FrontUserServiceImpl implements FrontUserService {
    @Autowired
    private FrontUserDao frontUserDao;
    @Override
    public void userRegister(User user) {
        user.setAuthority(1);
        user.setCreateTime(new Date());
        user.setLastEditTime(new Date());
        user.setImage("/images/user.png");
        frontUserDao.userRegister(user);

    }

    @Override
    public Integer queryUserNameCount(String username) {
        return frontUserDao.queryUserNameCount(username);
    }

    @Override
    public User checkUser(String username, String password) {
        password = MD5Util.md5(password);
        return frontUserDao.checkUser(username,password);
    }
}
