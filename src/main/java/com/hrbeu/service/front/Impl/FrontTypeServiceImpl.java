package com.hrbeu.service.front.Impl;

import com.hrbeu.dao.front.FrontTypeDao;
import com.hrbeu.pojo.Type;
import com.hrbeu.pojo.vo.Type_Count;
import com.hrbeu.service.front.FrontTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname FrontTypeServiceImpl
 * @Description TODO
 * @Date 2021/5/14 10:05
 * @Created by nxt
 */
@Service
public class FrontTypeServiceImpl implements FrontTypeService {
    @Autowired
    private FrontTypeDao frontTypeDao;

    @Override
    public List<Type_Count> queryTypeAndCount() {
        //查询全部的type
        List<Type> typeList = frontTypeDao.queryTypeList();
        if(typeList!=null){
            //定义一个Type_Count 的 List
            List<Type_Count> typeCountList = new ArrayList<>();
            //遍历所有type
            for (Type type : typeList) {
                //查询所有type中与发布的文档关联的数量
                Integer count = frontTypeDao.queryTypeCountInPublishedDocument(type.getTypeId());
                if(count!=null&&!count.equals(0)){
                    Type_Count type_count = new Type_Count();
                    type_count.setTypeId(type.getTypeId());
                    type_count.setTypeName(type.getTypeName());
                    type_count.setCount(count);
                    typeCountList.add(type_count);
                }else {
                    //如果没有关联，直接跳过
                    continue;
                }
            }
            return typeCountList;
        }
        return null;
    }

    @Override
    public List<Type> queryAllType() {
        return frontTypeDao.queryTypeList();
    }
}
