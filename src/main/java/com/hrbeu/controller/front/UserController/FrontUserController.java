package com.hrbeu.controller.front.UserController;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrbeu.pojo.User;
import com.hrbeu.service.front.FrontUserService;
import com.hrbeu.utils.MD5Util;
import com.hrbeu.utils.RedisUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FrontUserController {
    @Autowired
    private FrontUserService frontUserService;
    @Autowired
    private RedisUtil redisUtil;
    //注册
    @PostMapping("/register")
    public String register(@Param("username")String username, @Param("password")String password, @Param("email")String email, @Param("nickname")String nickname, Model model, HttpServletRequest request){
        String password_Md5 = MD5Util.md5(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(password_Md5);
        user.setEmail(email);
        user.setNickname(nickname);
        try {
            frontUserService.userRegister(user);
        }catch (Exception e){
            model.addAttribute("errMsg","注册失败，请重试");
            return "front/plt-register";
        }
        HttpSession session = request.getSession();
        user.setPassword(null);
        session.setAttribute("user",user);
        return "redirect:/front/index";

    }

    //检查用户名是否已经存在
    @GetMapping("/checkusername")
    @ResponseBody
    public Map<String,Boolean> checkUsername(@Param("username")String username){
        Map<String,Boolean> map = new HashMap<>();
        Integer userCount  = frontUserService.queryUserNameCount(username);
        if(userCount==null||userCount.equals(0)){
            map.put("success",true);
        }else {
            map.put("success",false);
        }
        return map;
    }

    //登录
    @PostMapping("/login")
    public ModelAndView login(@Param("username")String username, @Param("password")String password, HttpServletRequest request){
        ModelAndView model = new ModelAndView();
        //查询用户
        User user = frontUserService.checkUser(username,password);
        //如果不存在user，则用户名或密码错误
        if(user==null){
            model.addObject("errMsg","用户名不存在或密码错误");
            model.setViewName("/front/login");
            return model;
        }else {
            //将密码改为null
            user.setPassword(null);
            //存入session中
            HttpSession session = request.getSession();
            session.setAttribute("user",user);
        }
        model.setViewName("redirect:/front/index");
        return model;
    }

    //登出
    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        return "redirect:/front/login";
    }

    @PostMapping("/verifyCode")
    @ResponseBody
    public String verifyCode(@RequestParam("mobile")String mobile) throws JsonProcessingException {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "<accessKeyId>", "<accessSecret>");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return null;

    }
}
