package com.hrbeu.dao.admin;

import com.hrbeu.pojo.Type;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Classname AdminTypeDao
 * @Description TODO
 * @Date 2021/5/9 21:24
 * @Created by nxt
 */
@Repository
public interface AdminTypeDao {
    List<Type> queryAllTypes();
    Type queryTypeById(@Param("typeId") Long typeId);
    int queryAllTypeCount();
    List<Type> queryTypeListByPage(@Param("begin") int begin,@Param("pageSize") int pageSize);
    void updateTypeName(@Param("typeId") Long typeId,@Param("typeName") String typeName);
    void deleteTypeById(@Param("typeId") Long typeId);
    void addType(@Param("typeName") String typeName);
}
