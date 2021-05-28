package com.hrbeu.dao.admin;

import com.hrbeu.pojo.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @Classname AdminUserDao
 * @Description TODO
 * @Date 2021/5/9 20:33
 * @Created by nxt
 */
@Repository
public interface AdminUserDao {
    User queryUser(@Param("username") String username);
}
