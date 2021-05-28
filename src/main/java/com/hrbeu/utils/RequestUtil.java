package com.hrbeu.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Classname RequestUtil
 * @Description TODO
 * @Date 2021/5/10 11:21
 * @Created by nxt
 */
public class RequestUtil {
    public static Integer isRequestIsOn(Integer value,HttpServletRequest request,String paramName){
        if (request.getParameter(paramName)!=null&&request.getParameter(paramName).equals("on")){
            value =1;
        }
        return value;
    }
}
