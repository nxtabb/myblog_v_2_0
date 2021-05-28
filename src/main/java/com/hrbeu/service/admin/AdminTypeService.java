package com.hrbeu.service.admin;

import com.hrbeu.pojo.Type;
import com.hrbeu.pojo.vo.Type_Count;

import java.util.List;

/**
 * @Classname AdminTypeService
 * @Description TODO
 * @Date 2021/5/9 21:20
 * @Created by nxt
 */
public interface AdminTypeService {
    List<Type> queryAllTypes();
    Type queryTypeById(Long typeId);
    int queryAllTypeCount();
    List<Type> queryTypeListByPage(int pageIndex, int pageSize);
    List<Type_Count> queryTypeCountListByPage(int pageIndex, int pageSize);
    void updateTypeName(Long typeId, String typeName);
    void deleteTypeById(Long typeId);
    Integer queryTypeCountById(Long typeId);
    void addType(String name);
}
