package com.hrbeu.dao.front;

import com.hrbeu.pojo.Type;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Classname FrontTypeDao
 * @Description TODO
 * @Date 2021/5/14 10:06
 * @Created by nxt
 */
@Repository
public interface FrontTypeDao {
    List<Type> queryTypeList();
    Integer queryTypeCountInPublishedDocument(@Param("typeId") Long typeId);
}
