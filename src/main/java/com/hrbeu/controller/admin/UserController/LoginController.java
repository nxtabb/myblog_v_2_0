package com.hrbeu.controller.admin.UserController;

import com.hrbeu.pojo.User;
import com.hrbeu.service.admin.AdminUserService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/admin")
public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    AdminUserService adminUserService;
    //登录,检查用户名密码是否正确，如果正确，则跳转到index页，如果不正确，则跳转到登录页面
    @PostMapping("/login")
    public ModelAndView checkAndToIndex(@Param("username")String username,
                                        @Param("password")String password,
                                        HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        //查看是否存在用户
        User user = adminUserService.checkUser(username, password);
        if(user==null){
            model.addObject("errMsg","用户名不存在或密码错误");
            model.setViewName("admin/login");
            return model;
        }else {
            if (user.getAuthority() != 2){
                model.addObject("errMsg","当前用户不是管理员用户，不能进入后台管理");
                model.setViewName("admin/login");
                return model;
            } else {
                //登录成功
                user.setPassword(null);
                HttpSession session = request.getSession();
                session.setAttribute("user",user);
                model.setViewName("redirect:/admin/index");
            }
        }
        return model;
    }
}
