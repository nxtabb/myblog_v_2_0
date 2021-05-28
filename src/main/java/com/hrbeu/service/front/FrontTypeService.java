package com.hrbeu.service.front;

import com.hrbeu.pojo.Type;
import com.hrbeu.pojo.vo.Type_Count;

import java.util.List;

/**
 * @Classname FrontTypeService
 * @Description TODO
 * @Date 2021/5/14 09:58
 * @Created by nxt
 */
public interface FrontTypeService {
    List<Type_Count> queryTypeAndCount();
    List<Type> queryAllType();
}
